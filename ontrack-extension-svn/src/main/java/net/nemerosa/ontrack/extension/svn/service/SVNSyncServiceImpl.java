package net.nemerosa.ontrack.extension.svn.service;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import net.nemerosa.ontrack.extension.svn.db.SVNEventDao;
import net.nemerosa.ontrack.extension.svn.db.SVNRepository;
import net.nemerosa.ontrack.extension.svn.db.TCopyEvent;
import net.nemerosa.ontrack.extension.svn.model.SVNRevisionInfo;
import net.nemerosa.ontrack.extension.svn.model.SVNSyncInfoStatus;
import net.nemerosa.ontrack.extension.svn.property.*;
import net.nemerosa.ontrack.extension.svn.support.SVNUtils;
import net.nemerosa.ontrack.model.security.BuildCreate;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.*;
import net.nemerosa.ontrack.model.support.ApplicationInfo;
import net.nemerosa.ontrack.model.support.ApplicationInfoProvider;
import net.nemerosa.ontrack.model.support.ScheduledService;
import net.nemerosa.ontrack.model.support.Time;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.Trigger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class SVNSyncServiceImpl implements SVNSyncService, ApplicationInfoProvider, ScheduledService {

    private final Logger logger = LoggerFactory.getLogger(SVNSyncService.class);

    private final StructureService structureService;
    private final PropertyService propertyService;
    private final SecurityService securityService;
    private final SVNService svnService;
    private final SVNEventDao eventDao;
    private final TransactionTemplate transactionTemplate;

    /**
     * Index of jobs per branches
     */
    private final Map<ID, SyncJob> jobs = new ConcurrentHashMap<>();

    /**
     * Runner for the synchronisation jobs
     */
    private final ExecutorService executor = Executors.newFixedThreadPool(
            5,
            new ThreadFactoryBuilder()
                    .setDaemon(true)
                    .setNameFormat("Synchronisation %s")
                    .build()
    );

    @Autowired
    public SVNSyncServiceImpl(
            StructureService structureService,
            PropertyService propertyService,
            SecurityService securityService,
            SVNService svnService,
            SVNEventDao eventDao,
            PlatformTransactionManager transactionManager) {
        this.structureService = structureService;
        this.propertyService = propertyService;
        this.securityService = securityService;
        this.svnService = svnService;
        this.eventDao = eventDao;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    @Override
    public SVNSyncInfoStatus launchSync(ID branchId) {
        // Checks any current job
        if (jobs.containsKey(branchId)) {
            return SVNSyncInfoStatus.of(branchId).withMessage("A synchronisation is already running for this branch.");
        }
        // Loads the branch
        Branch branch = structureService.getBranch(branchId);
        // Checks the accesses
        securityService.checkProjectFunction(branch.projectId(), BuildCreate.class);
        // Gets the configuration property
        Property<SVNSyncProperty> syncProperty = propertyService.getProperty(branch, SVNSyncPropertyType.class);
        if (syncProperty.isEmpty()) {
            return SVNSyncInfoStatus.of(branchId).withMessage("The synchronisation has not been configured for this branch.");
        }
        // Gets the project configurations for SVN
        Property<SVNProjectConfigurationProperty> projectConfigurationProperty =
                propertyService.getProperty(branch.getProject(), SVNProjectConfigurationPropertyType.class);
        if (projectConfigurationProperty.isEmpty()) {
            return SVNSyncInfoStatus.of(branchId).withMessage("SVN has not been configured for this branch's project.");
        }
        // Gets the branch configurations for SVN
        Property<SVNBranchConfigurationProperty> branchConfigurationProperty =
                propertyService.getProperty(branch, SVNBranchConfigurationPropertyType.class);
        if (branchConfigurationProperty.isEmpty()) {
            return SVNSyncInfoStatus.of(branchId).withMessage("SVN has not been configured for this branch.");
        }
        // Cannot work with revisions only
        if (SVNUtils.isPathRevision(branchConfigurationProperty.getValue().getBuildPath())) {
            return SVNSyncInfoStatus.of(branchId).withMessage("The build path for the branch is not correctly configured.");
        }
        // Creates a new job for this branch
        SyncJob job = new SyncJob(branch,
                projectConfigurationProperty.getValue(),
                branchConfigurationProperty.getValue(),
                syncProperty.getValue());
        // Submits the job
        executor.submit(job);
        // Indexes the job
        jobs.put(branchId, job);
        // Returns its status
        return job.getStatus();
    }

    @Override
    public List<ApplicationInfo> getApplicationInfoList() {
        return jobs.values().stream()
                .map(SyncJob::getApplicationInfo)
                .collect(Collectors.toList());
    }

    protected void runSync() {
        // TODO Sync must be disabled when the SVN Extension is not enabled
        // For all SVN-configured branches
        getSVNConfiguredBranches().forEach(branch -> {
            Property<SVNSyncProperty> svnSync = propertyService.getProperty(branch, SVNSyncPropertyType.class);
            // ... if the SVN sync is configured
            if (!svnSync.isEmpty() && svnSync.getValue().getInterval() > 0) {
                // ... launches the synchronisation
                logger.info("[svn-sync] Launching synchronisation for branch {}/{} ({})",
                        branch.getProject().getName(),
                        branch.getName(),
                        branch.id());
                launchSync(branch.getId());
            }
        });
    }

    @Override
    public Runnable getTask() {
        return () -> {
            logger.info("[svn-sync] Launching synchronisation");
            securityService.asAdmin(this::runSync);
        };
    }

    @Override
    public Trigger getTrigger() {
        return triggerContext -> {
            // Gets the mimimum of the scan intervals (outside of 0)
            OptionalInt scanInterval = securityService.asAdmin(this::getMinScanInterval);
            // No scan, tries again in one minute, in case the configuration has changed
            if (!scanInterval.isPresent() || scanInterval.getAsInt() <= 0) {
                return Time.toJavaUtilDate(Time.now().plusMinutes(1));
            } else {
                // Last execution time
                Date time = triggerContext.lastActualExecutionTime();
                if (time != null) {
                    return Time.toJavaUtilDate(Time.from(time, null).plusMinutes(scanInterval.getAsInt()));
                } else {
                    // Never executed before
                    return Time.toJavaUtilDate(Time.now().plusMinutes(scanInterval.getAsInt()));
                }
            }
        };
    }

    protected OptionalInt getMinScanInterval() {
        // List of all the SVN-configured branches
        return getSVNConfiguredBranches()
                // ...gets their SVN sync property
                .map(branch -> propertyService.getProperty(branch, SVNSyncPropertyType.class))
                        // ...retains the branches which have actually a SVN sync configured with interval > 0
                .filter(property -> !property.isEmpty() && property.getValue().getInterval() > 0)
                        // ...gets the intervals
                .mapToInt(property -> property.getValue().getInterval())
                        // ... gets the minimum
                .min();
    }

    /**
     * Gets the list of all branches, for all projects, which are properly configured for SVN.
     */
    protected Stream<Branch> getSVNConfiguredBranches() {
        return structureService.getProjectList()
                .stream()
                        // ...which have a SVN configuration
                .filter(project -> propertyService.hasProperty(project, SVNProjectConfigurationPropertyType.class))
                        // ...gets all their branches
                .flatMap(project -> structureService.getBranchesForProject(project.getId()).stream())
                        // ...which have a SVN configuration
                .filter(branch -> propertyService.hasProperty(branch, SVNBranchConfigurationPropertyType.class));
    }

    private class SyncJob implements Runnable {

        private final Branch branch;
        private final SVNProjectConfigurationProperty projectConfigurationProperty;
        private final SVNBranchConfigurationProperty branchConfigurationProperty;
        private final SVNSyncProperty syncProperty;

        /**
         * Number of created builds
         */
        private final AtomicInteger createdBuilds = new AtomicInteger();

        public SyncJob(Branch branch, SVNProjectConfigurationProperty projectConfigurationProperty, SVNBranchConfigurationProperty branchConfigurationProperty, SVNSyncProperty syncProperty) {
            this.branch = branch;
            this.projectConfigurationProperty = projectConfigurationProperty;
            this.branchConfigurationProperty = branchConfigurationProperty;
            this.syncProperty = syncProperty;
        }

        @Override
        public void run() {
            securityService.asAdmin(this::doRun);
        }

        private boolean doRun() {
            try {
                // Gets the build path
                String buildPathPattern = branchConfigurationProperty.getBuildPath();
                // Gets the directory to look the tags from
                String basePath = SVNUtils.getBasePath(buildPathPattern);
                // SVN repository configuration
                SVNRepository repository = svnService.getRepository(projectConfigurationProperty.getConfiguration().getName());
                // Gets the list of tags from the copy events, filtering them
                List<TCopyEvent> copies = eventDao.findCopies(
                        // In this repository
                        repository.getId(),
                        // from path...
                        branchConfigurationProperty.getBranchPath(),
                        // to path with prefix...
                        basePath,
                        // filter the target path with...
                        (copyEvent) -> SVNUtils.followsBuildPattern(copyEvent.copyToLocation(), buildPathPattern)
                );
                // Creates the builds (in a transaction)
                for (TCopyEvent copy : copies) {
                    Optional<Build> build = transactionTemplate.execute(status -> createBuild(copy, buildPathPattern, repository));
                    // Completes the information collection (build created)
                    if (build.isPresent()) {
                        createdBuilds.incrementAndGet();
                    }
                }
                // :)
                return true;
            } catch (Exception ex) {
                // TODO Logs using the future ApplicationLogService
                // In the meantime, just logs in the console...
                logger.error(
                        String.format("[svn-sync] Error for branch %s/%s (%d)",
                                branch.getProject().getName(),
                                branch.getName(),
                                branch.id()),
                        ex
                );
                // :(
                return false;
            } finally {
                // Removes this job from the list after completion
                jobs.remove(branch.getId());
            }
        }

        private Optional<Build> createBuild(TCopyEvent copy, String buildPathPattern, SVNRepository repository) {
            // Extracts the build name from the copyTo path
            String buildName = SVNUtils.getBuildName(copy.copyToLocation(), buildPathPattern);
            // Gets an existing build if any
            Optional<Build> build = structureService.findBuildByName(branch.getProject().getName(), branch.getName(), buildName);
            // If none exists, just creates it
            if (!build.isPresent()) {
                logger.debug("[svn-sync] Build {} does not exist - creating.");
                return Optional.of(doCreateBuild(copy, buildName, repository));
            }
            // If Ok to override, deletes it and creates it
            else if (syncProperty.isOverride()) {
                logger.debug("[svn-sync] Build {} already exists - overriding.");
                // Deletes the build
                structureService.deleteBuild(build.get().getId());
                // Creates the build
                return Optional.of(doCreateBuild(copy, buildName, repository));
            }
            // Else, just puts some log entry
            else {
                logger.debug("[svn-sync] Build {} already exists - not overriding.");
                return Optional.empty();
            }
        }

        private Build doCreateBuild(TCopyEvent copy, String buildName, SVNRepository repository) {
            // The build date is assumed to be the creation of the tag
            SVNRevisionInfo revisionInfo = svnService.getRevisionInfo(repository, copy.getRevision());
            LocalDateTime revisionTime = revisionInfo.getDateTime();
            // Creation of the build
            return structureService.newBuild(
                    Build.of(
                            branch,
                            new NameDescription(
                                    buildName,
                                    String.format("Build created by SVN synchronisation from tag %s", copy.getCopyToPath())
                            ),
                            securityService.getCurrentSignature().withTime(revisionTime)
                    )
            );
        }

        public SVNSyncInfoStatus getStatus() {
            return SVNSyncInfoStatus.of(branch.getId());
        }

        public ApplicationInfo getApplicationInfo() {
            return ApplicationInfo.info(
                    String.format(
                            "Running build synchronisation from SVN for branch %s/%s: %d build(s) created",
                            branch.getProject().getName(),
                            branch.getName(),
                            createdBuilds.get()
                    )
            );
        }
    }
}

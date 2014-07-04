package net.nemerosa.ontrack.extension.svn.service;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import net.nemerosa.ontrack.extension.svn.db.SVNEventDao;
import net.nemerosa.ontrack.extension.svn.db.TCopyEvent;
import net.nemerosa.ontrack.extension.svn.model.SVNSyncInfoStatus;
import net.nemerosa.ontrack.extension.svn.property.*;
import net.nemerosa.ontrack.extension.svn.support.SVNUtils;
import net.nemerosa.ontrack.model.security.BuildCreate;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.*;
import net.nemerosa.ontrack.model.support.ApplicationInfo;
import net.nemerosa.ontrack.model.support.ApplicationInfoProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class SVNSyncServiceImpl implements SVNSyncService, ApplicationInfoProvider {

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
            // Gets the build path
            String buildPathPattern = branchConfigurationProperty.getBuildPath();
            // Cannot work with revisions only
            if (SVNUtils.isPathRevision(buildPathPattern)) {
                // TODO Logs an error
                // ... and exits
                return;
            }
            // Gets the directory to look the tags from
            String basePath = SVNUtils.getBasePath(buildPathPattern);
            // Gets the list of tags from the copy events, filtering them
            List<TCopyEvent> copies = eventDao.findCopies(
                    // In this repository
                    svnService.getRepository(projectConfigurationProperty.getConfiguration().getName()).getId(),
                    // from path...
                    branchConfigurationProperty.getBranchPath(),
                    // to path with prefix...
                    basePath,
                    // filter the target path with...
                    (copyEvent) -> SVNUtils.followsBuildPattern(copyEvent.copyToLocation(), buildPathPattern)
            );
            // Creates the builds (in a transaction)
            for (TCopyEvent copy : copies) {
                Optional<Build> build = transactionTemplate.execute(status -> createBuild(copy, buildPathPattern));
                // Completes the information collection (build created)
                createdBuilds.incrementAndGet();
            }
            // Removes this job from the list after completion
            jobs.remove(branch.getId());
        }

        private Optional<Build> createBuild(TCopyEvent copy, String buildPathPattern) {
            // Extracts the build name from the copyTo path
            String buildName = SVNUtils.getBuildName(copy.copyToLocation(), buildPathPattern);
            // Gets an existing build if any
            Optional<Build> build = structureService.findBuildByName(branch.getProject().getName(), branch.getName(), buildName);
            // If none exists, just creates it
            if (!build.isPresent()) {
                logger.debug("[svn-sync] Build {} does not exist - creating.");
                return Optional.of(doCreateBuild(copy, buildName));
            }
            // If Ok to override, deletes it and creates it
            else if (syncProperty.isOverride()) {
                logger.debug("[svn-sync] Build {} already exists - overriding.");
                // Deletes the build
                // Uses admin rights for this?
                securityService.asAdmin(() -> structureService.deleteBuild(build.get().getId()));
                // Creates the build
                return Optional.of(doCreateBuild(copy, buildName));
            }
            // Else, just puts some log entry
            else {
                logger.debug("[svn-sync] Build {} already exists - not overriding.");
                return Optional.empty();
            }
        }

        private Build doCreateBuild(TCopyEvent copy, String buildName) {
            return structureService.newBuild(
                    Build.of(
                            branch,
                            new NameDescription(
                                    buildName,
                                    String.format("Build created by SVN synchronisation from tag %s", copy.getCopyToPath())
                            ),
                            securityService.getCurrentSignature()
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

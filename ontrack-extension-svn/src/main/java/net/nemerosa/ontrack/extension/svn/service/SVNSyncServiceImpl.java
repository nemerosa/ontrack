package net.nemerosa.ontrack.extension.svn.service;

import net.nemerosa.ontrack.extension.svn.db.SVNEventDao;
import net.nemerosa.ontrack.extension.svn.db.SVNRepository;
import net.nemerosa.ontrack.extension.svn.db.TCopyEvent;
import net.nemerosa.ontrack.extension.svn.model.SVNRevisionInfo;
import net.nemerosa.ontrack.extension.svn.model.SVNSyncInfoStatus;
import net.nemerosa.ontrack.extension.svn.property.*;
import net.nemerosa.ontrack.extension.svn.support.SVNUtils;
import net.nemerosa.ontrack.model.job.Job;
import net.nemerosa.ontrack.model.job.JobProvider;
import net.nemerosa.ontrack.model.job.JobTask;
import net.nemerosa.ontrack.model.job.RunnableJobTask;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// TODO Manual launch by queuing

@Service
public class SVNSyncServiceImpl implements SVNSyncService, JobProvider {

    private final Logger logger = LoggerFactory.getLogger(SVNSyncService.class);

    private final StructureService structureService;
    private final PropertyService propertyService;
    private final SecurityService securityService;
    private final SVNService svnService;
    private final SVNEventDao eventDao;
    private final TransactionTemplate transactionTemplate;

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
        throw new RuntimeException("NYI");
    }

    protected void sync(Branch branch, Consumer<String> info) {
        // Number of created builds
        AtomicInteger createdBuilds = new AtomicInteger();
        // Gets the configuration property
        SVNSyncProperty syncProperty = propertyService.getProperty(branch, SVNSyncPropertyType.class).getValue();
        // Gets the project configurations for SVN
        SVNProjectConfigurationProperty projectConfigurationProperty = propertyService.getProperty(branch.getProject(), SVNProjectConfigurationPropertyType.class).getValue();
        // Gets the branch configurations for SVN
        SVNBranchConfigurationProperty branchConfigurationProperty = propertyService.getProperty(branch, SVNBranchConfigurationPropertyType.class).getValue();
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
            Optional<Build> build = transactionTemplate.execute(status -> createBuild(syncProperty, branch, copy, buildPathPattern, repository));
            // Completes the information collection (build created)
            if (build.isPresent()) {
                int count = createdBuilds.incrementAndGet();
                info.accept(String.format(
                        "Running build synchronisation from SVN for branch %s/%s: %d build(s) created",
                        branch.getProject().getName(),
                        branch.getName(),
                        count
                ));
            }
        }
    }

    private Optional<Build> createBuild(SVNSyncProperty syncProperty, Branch branch, TCopyEvent copy, String buildPathPattern, SVNRepository repository) {
        // Extracts the build name from the copyTo path
        String buildName = SVNUtils.getBuildName(copy.copyToLocation(), buildPathPattern);
        // Gets an existing build if any
        Optional<Build> build = structureService.findBuildByName(branch.getProject().getName(), branch.getName(), buildName);
        // If none exists, just creates it
        if (!build.isPresent()) {
            logger.debug("[svn-sync] Build {} does not exist - creating.", buildName);
            return Optional.of(doCreateBuild(branch, copy, buildName, repository));
        }
        // If Ok to override, deletes it and creates it
        else if (syncProperty.isOverride()) {
            logger.debug("[svn-sync] Build {} already exists - overriding.", buildName);
            // Deletes the build
            structureService.deleteBuild(build.get().getId());
            // Creates the build
            return Optional.of(doCreateBuild(branch, copy, buildName, repository));
        }
        // Else, just puts some log entry
        else {
            logger.debug("[svn-sync] Build {} already exists - not overriding.", buildName);
            return Optional.empty();
        }
    }

    private Build doCreateBuild(Branch branch, TCopyEvent copy, String buildName, SVNRepository repository) {
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

    @Override
    public Collection<Job> getJobs() {
        return getSVNConfiguredBranches()
                .filter(branch -> {
                    Property<SVNSyncProperty> svnSync = propertyService.getProperty(branch, SVNSyncPropertyType.class);
                    return !svnSync.isEmpty() && svnSync.getValue().getInterval() > 0;
                })
                .map(this::createJob)
                .collect(Collectors.toList());
    }

    protected Job createJob(Branch branch) {
        Property<SVNSyncProperty> svnSync = propertyService.getProperty(branch, SVNSyncPropertyType.class);
        return new Job() {
            @Override
            public String getCategory() {
                return "SVNSync";
            }

            @Override
            public String getId() {
                return String.valueOf(branch.getId());
            }

            @Override
            public String getDescription() {
                return String.format(
                        "Synchronisation of builds with SVN for branch %s/%s",
                        branch.getProject().getName(),
                        branch.getName()
                );
            }

            @Override
            public int getInterval() {
                return svnSync.getValue().getInterval();
            }

            @Override
            public JobTask createTask() {
                return new RunnableJobTask(info -> sync(branch, info));
            }
        };
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
}

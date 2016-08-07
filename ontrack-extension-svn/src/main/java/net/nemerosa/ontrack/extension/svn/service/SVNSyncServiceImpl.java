package net.nemerosa.ontrack.extension.svn.service;

import net.nemerosa.ontrack.extension.svn.SubversionConfProperties;
import net.nemerosa.ontrack.extension.svn.db.SVNEventDao;
import net.nemerosa.ontrack.extension.svn.db.SVNRepository;
import net.nemerosa.ontrack.extension.svn.db.TCopyEvent;
import net.nemerosa.ontrack.extension.svn.model.*;
import net.nemerosa.ontrack.extension.svn.property.*;
import net.nemerosa.ontrack.extension.svn.support.ConfiguredBuildSvnRevisionLink;
import net.nemerosa.ontrack.job.*;
import net.nemerosa.ontrack.job.orchestrator.JobOrchestratorSupplier;
import net.nemerosa.ontrack.model.security.BuildCreate;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.*;
import net.nemerosa.ontrack.model.support.AbstractBranchJob;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

@Service
public class SVNSyncServiceImpl implements SVNSyncService, JobOrchestratorSupplier {

    private static final JobType SVN_BUILD_SYNC_JOB =
            SVNService.SVN_JOB_CATEGORY.getType("svn-build-sync");

    private final Logger logger = LoggerFactory.getLogger(SVNSyncService.class);

    private final StructureService structureService;
    private final PropertyService propertyService;
    private final SecurityService securityService;
    private final SVNService svnService;
    private final SVNEventDao eventDao;
    private final TransactionTemplate transactionTemplate;
    private final JobScheduler jobScheduler;
    private final BuildSvnRevisionLinkService buildSvnRevisionLinkService;
    private final SubversionConfProperties subversionConfProperties;

    @Autowired
    public SVNSyncServiceImpl(
            StructureService structureService,
            PropertyService propertyService,
            SecurityService securityService,
            SVNService svnService,
            SVNEventDao eventDao,
            PlatformTransactionManager transactionManager,
            JobScheduler jobScheduler,
            BuildSvnRevisionLinkService buildSvnRevisionLinkService, SubversionConfProperties subversionConfProperties) {
        this.structureService = structureService;
        this.propertyService = propertyService;
        this.securityService = securityService;
        this.svnService = svnService;
        this.eventDao = eventDao;
        this.jobScheduler = jobScheduler;
        this.buildSvnRevisionLinkService = buildSvnRevisionLinkService;
        this.subversionConfProperties = subversionConfProperties;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    @Override
    public SVNSyncInfoStatus launchSync(ID branchId) {
        Branch branch = structureService.getBranch(branchId);
        // Security check
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
        // Gets the build link
        ConfiguredBuildSvnRevisionLink<Object> revisionLink = buildSvnRevisionLinkService.getConfiguredBuildSvnRevisionLink(branchConfigurationProperty.getValue().getBuildRevisionLink());
        // Cannot work with revisions only
        if (!(revisionLink instanceof IndexableBuildSvnRevisionLink)) {
            return SVNSyncInfoStatus.of(branchId).withMessage("The build path for the branch is not correctly configured.");
        }
        // Queue a new job
        jobScheduler.fireImmediately(getSvnBuildSyncJobKey(branch));
        // OK
        return SVNSyncInfoStatus.of(branchId);
    }

    protected void sync(Branch branch, JobRunListener runListener) {
        // Number of created builds
        AtomicInteger createdBuilds = new AtomicInteger();
        // Gets the configuration property
        SVNSyncProperty syncProperty = propertyService.getProperty(branch, SVNSyncPropertyType.class).getValue();
        // Gets the project configurations for SVN
        SVNProjectConfigurationProperty projectConfigurationProperty = propertyService.getProperty(branch.getProject(), SVNProjectConfigurationPropertyType.class).getValue();
        // Gets the branch configurations for SVN
        SVNBranchConfigurationProperty branchConfigurationProperty = propertyService.getProperty(branch, SVNBranchConfigurationPropertyType.class).getValue();
        // SVN repository configuration
        SVNRepository repository = svnService.getRepository(projectConfigurationProperty.getConfiguration().getName());
        // Link
        ConfiguredBuildSvnRevisionLink<?> revisionLink = buildSvnRevisionLinkService.getConfiguredBuildSvnRevisionLink(branchConfigurationProperty.getBuildRevisionLink());
        // Gets the base path
        svnService.getBasePath(repository, branchConfigurationProperty.getCuredBranchPath()).ifPresent(basePath -> {
            // Tags path
            String tagsPath = basePath + "/tags";
            // Gets the list of tags from the copy events, filtering them
            List<TCopyEvent> copies = eventDao.findCopies(
                    // In this repository
                    repository.getId(),
                    // from path...
                    branchConfigurationProperty.getCuredBranchPath(),
                    // to path with prefix...
                    tagsPath,
                    // filter the target path with...
                    (copyEvent) -> getBuildNameFromPath(tagsPath, revisionLink, copyEvent.copyToLocation()).isPresent()
            );
            // Creates the builds (in a transaction)
            for (TCopyEvent copy : copies) {
                Optional<Build> build = transactionTemplate.execute(status -> createBuild(tagsPath, syncProperty, branch, copy, revisionLink, repository));
                // Completes the information collection (build created)
                if (build.isPresent()) {
                    int count = createdBuilds.incrementAndGet();
                    runListener.message(
                            "Running build synchronisation from SVN for branch %s/%s: %d build(s) created",
                            branch.getProject().getName(),
                            branch.getName(),
                            count
                    );
                }
            }
        });
    }

    private Optional<String> getBuildNameFromPath(String tagsPath, ConfiguredBuildSvnRevisionLink<?> revisionLink, SVNLocation location) {
        String path = location.getPath();
        if (StringUtils.startsWith(path, tagsPath)) {
            String tagName = StringUtils.strip(
                    StringUtils.substringAfter(path, tagsPath),
                    "/"
            );
            return revisionLink.getBuildNameFromTagName(tagName);
        } else {
            return Optional.empty();
        }
    }

    private Optional<Build> createBuild(String tagsPath, SVNSyncProperty syncProperty, Branch branch, TCopyEvent copy, ConfiguredBuildSvnRevisionLink<?> revisionLink, SVNRepository repository) {
        // Extracts the build name from the copyTo path
        return getBuildNameFromPath(tagsPath, revisionLink, copy.copyToLocation())
                .flatMap(buildName -> {
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
                });
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
    public Stream<JobRegistration> collectJobRegistrations() {
        if (subversionConfProperties.isBuildSyncDisabled()) {
            return Stream.empty();
        } else {
            return securityService.asAdmin(() ->
                    getSVNConfiguredBranches()
                            .filter(branch -> propertyService.getProperty(branch, SVNSyncPropertyType.class).option().isPresent())
                            .map(this::getSVNBuildSyncJobRegistration)
            );
        }
    }

    private JobRegistration getSVNBuildSyncJobRegistration(Branch branch) {
        Property<SVNSyncProperty> svnSync = propertyService.getProperty(branch, SVNSyncPropertyType.class);
        if (svnSync.isEmpty()) {
            throw new IllegalStateException("No SVN build sync is set");
        } else {
            return JobRegistration.of(createJob(branch))
                    .everyMinutes(svnSync.getValue().getInterval());
        }
    }

    protected Job createJob(Branch branch) {
        return new AbstractBranchJob(structureService, branch) {

            @Override
            public JobKey getKey() {
                return getSvnBuildSyncJobKey(branch);
            }

            @Override
            public JobRun getTask() {
                return runListener -> sync(branch, runListener);
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
            public boolean isValid() {
                return super.isValid() &&
                        propertyService.hasProperty(branch, SVNSyncPropertyType.class) &&
                        svnService.getSVNRepository(branch).isPresent()
                        ;
            }
        };
    }

    protected JobKey getSvnBuildSyncJobKey(Branch branch) {
        return SVN_BUILD_SYNC_JOB.getKey(String.valueOf(branch.getId()));
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

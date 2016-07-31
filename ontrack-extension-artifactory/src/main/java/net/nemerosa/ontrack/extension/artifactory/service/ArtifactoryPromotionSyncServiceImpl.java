package net.nemerosa.ontrack.extension.artifactory.service;

import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.extension.artifactory.ArtifactoryConfProperties;
import net.nemerosa.ontrack.extension.artifactory.client.ArtifactoryClient;
import net.nemerosa.ontrack.extension.artifactory.client.ArtifactoryClientFactory;
import net.nemerosa.ontrack.extension.artifactory.configuration.ArtifactoryConfiguration;
import net.nemerosa.ontrack.extension.artifactory.configuration.ArtifactoryConfigurationService;
import net.nemerosa.ontrack.extension.artifactory.model.ArtifactoryStatus;
import net.nemerosa.ontrack.extension.artifactory.property.ArtifactoryPromotionSyncProperty;
import net.nemerosa.ontrack.extension.artifactory.property.ArtifactoryPromotionSyncPropertyType;
import net.nemerosa.ontrack.job.*;
import net.nemerosa.ontrack.job.orchestrator.JobOrchestratorSupplier;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.*;
import net.nemerosa.ontrack.model.support.AbstractBranchJob;
import net.nemerosa.ontrack.model.support.ConfigurationServiceListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.apache.commons.lang3.StringUtils.replace;

@Service
public class ArtifactoryPromotionSyncServiceImpl implements ArtifactoryPromotionSyncService, JobOrchestratorSupplier, ConfigurationServiceListener<ArtifactoryConfiguration> {

    private static final JobType ARTIFACTORY_BUILD_SYNC_JOB =
            JobCategory.of("artifactory").withName("Artifactory")
                    .getType("build-sync").withName("Artifactory Build synchronisation");

    private final Logger logger = LoggerFactory.getLogger(ArtifactoryPromotionSyncServiceImpl.class);

    private final StructureService structureService;
    private final PropertyService propertyService;
    private final ArtifactoryClientFactory artifactoryClientFactory;
    private final ArtifactoryConfProperties artifactoryConfProperties;
    private final SecurityService securityService;

    @Autowired
    public ArtifactoryPromotionSyncServiceImpl(StructureService structureService, PropertyService propertyService, ArtifactoryClientFactory artifactoryClientFactory, ArtifactoryConfigurationService configurationService, ArtifactoryConfProperties artifactoryConfProperties, SecurityService securityService) {
        this.structureService = structureService;
        this.propertyService = propertyService;
        this.artifactoryClientFactory = artifactoryClientFactory;
        this.artifactoryConfProperties = artifactoryConfProperties;
        this.securityService = securityService;
        configurationService.addConfigurationServiceListener(this);
    }

    @Override
    public Stream<JobRegistration> collectJobRegistrations() {
        if (artifactoryConfProperties.isBuildSyncDisabled()) {
            return Stream.empty();
        } else {
            return securityService.asAdmin(() ->
                    // For all projects...
                    structureService.getProjectList().stream()
                            // ... and their branches
                            .flatMap(project -> structureService.getBranchesForProject(project.getId()).stream())
                            // ... only if not a template
                            .filter(branch -> branch.getType() != BranchType.TEMPLATE_DEFINITION)
                            // ... gets those with the sync. property
                            .filter(branch -> propertyService.hasProperty(branch, ArtifactoryPromotionSyncPropertyType.class))
                            // ... creates the job
                            .map(this::scheduleArtifactoryBuildSync)
            );
        }
    }

    public JobRegistration scheduleArtifactoryBuildSync(Branch branch) {
        ArtifactoryPromotionSyncProperty property = propertyService.getProperty(branch, ArtifactoryPromotionSyncPropertyType.class).getValue();
        return JobRegistration.of(getBranchSyncJob(branch)).everyMinutes(property.getInterval());
    }

    private JobKey getBranchSyncJobKey(Branch branch) {
        return ARTIFACTORY_BUILD_SYNC_JOB.getKey(branch.getId().toString());
    }

    private Job getBranchSyncJob(Branch branch) {
        return propertyService.getProperty(branch, ArtifactoryPromotionSyncPropertyType.class).option()
                .map(syncProperty ->
                        new AbstractBranchJob(structureService, branch) {
                            @Override
                            public JobKey getKey() {
                                return getBranchSyncJobKey(branch);
                            }

                            @Override
                            public JobRun getTask() {
                                return runListener -> sync(branch, runListener);
                            }

                            @Override
                            public String getDescription() {
                                return String.format(
                                        "Synchronisation of promotions with Artifactory for branch %s/%s",
                                        branch.getProject().getName(),
                                        branch.getName()
                                );
                            }

                            @Override
                            public boolean isValid() {
                                return super.isValid() &&
                                        propertyService.hasProperty(branch, ArtifactoryPromotionSyncPropertyType.class);
                            }
                        }
                )
                .orElseThrow(() -> new IllegalStateException("Branch not configured for Artifactory"));
    }

    private void sync(Branch branch, JobRunListener listener) {
        // Gets the sync properties
        Property<ArtifactoryPromotionSyncProperty> syncProperty = propertyService.getProperty(branch, ArtifactoryPromotionSyncPropertyType.class);
        if (syncProperty.isEmpty()) {
            throw new IllegalStateException(String.format("Cannot find sync. property on branch %d", branch.id()));
        }
        String buildName = syncProperty.getValue().getBuildName();
        String buildNameFilter = syncProperty.getValue().getBuildNameFilter();
        ArtifactoryConfiguration configuration = syncProperty.getValue().getConfiguration();
        String log = String.format("Sync branch %s/%s with Artifactory build %s (%s)",
                branch.getProject().getName(),
                branch.getName(),
                buildName,
                buildNameFilter);
        logger.info("[artifactory-sync] {}", log);
        listener.message(log);
        // Build name filter
        Pattern buildNamePattern = Pattern.compile(
                replace(replace(buildNameFilter, ".", "\\."), "*", ".*")
        );
        // Gets an Artifactory client
        ArtifactoryClient client = artifactoryClientFactory.getClient(configuration);
        // Gets all the build numbers for the specified build name
        List<String> buildNumbers = client.getBuildNumbers(buildName).stream()
                // ... and filter them
                .filter(name -> buildNamePattern.matcher(name).matches())
                .collect(Collectors.toList());
        // Synchronises the promotion levels for each build
        for (String buildNumber : buildNumbers) {
            syncBuild(branch, buildName, buildNumber, client, listener);
        }
    }

    protected void syncBuild(Branch branch, String artifactoryBuildName, String buildName, ArtifactoryClient client, JobRunListener listener) {
        // Looks for the build
        Optional<Build> buildOpt = structureService.findBuildByName(
                branch.getProject().getName(),
                branch.getName(),
                buildName
        );
        if (buildOpt.isPresent()) {
            // Log
            String log = String.format("Sync branch %s/%s for Artifactory build %s",
                    branch.getProject().getName(),
                    branch.getName(),
                    buildName);
            logger.debug("[artifactory-sync] {}", log);
            listener.message(log);
            // Gets the build information from Artifactory
            JsonNode buildInfo = client.getBuildInfo(artifactoryBuildName, buildName);
            // Gets the list of statuses
            List<ArtifactoryStatus> statuses = client.getStatuses(buildInfo);
            // For all statuses
            for (ArtifactoryStatus artifactoryStatus : statuses) {
                String statusName = artifactoryStatus.getName();
                // Looks for an existing promotion level with the same name on the branch
                Optional<PromotionLevel> promotionLevelOpt = structureService.findPromotionLevelByName(
                        branch.getProject().getName(),
                        branch.getName(),
                        statusName
                );
                if (promotionLevelOpt.isPresent()) {
                    // Looks for an existing promotion run for the build
                    Optional<PromotionRun> runOpt = structureService.getLastPromotionRunForBuildAndPromotionLevel(buildOpt.get(), promotionLevelOpt.get());
                    if (!runOpt.isPresent()) {
                        // No existing promotion, we can promote safely
                        logger.info("[artifactory-sync] Promote {}/{}/{} to {}",
                                branch.getProject().getName(),
                                branch.getName(),
                                buildName,
                                statusName);
                        // Actual promotion
                        structureService.newPromotionRun(
                                PromotionRun.of(
                                        buildOpt.get(),
                                        promotionLevelOpt.get(),
                                        Signature.of(artifactoryStatus.getUser()).withTime(artifactoryStatus.getTimestamp()),
                                        "Promoted from Artifactory"
                                )
                        );
                    }
                }
            }
        }
    }
}

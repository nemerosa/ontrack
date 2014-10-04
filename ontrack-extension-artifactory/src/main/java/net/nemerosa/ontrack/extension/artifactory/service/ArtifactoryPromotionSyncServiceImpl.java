package net.nemerosa.ontrack.extension.artifactory.service;

import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.extension.artifactory.client.ArtifactoryClient;
import net.nemerosa.ontrack.extension.artifactory.client.ArtifactoryClientFactory;
import net.nemerosa.ontrack.extension.artifactory.configuration.ArtifactoryConfiguration;
import net.nemerosa.ontrack.extension.artifactory.model.ArtifactoryStatus;
import net.nemerosa.ontrack.extension.artifactory.property.ArtifactoryPromotionSyncProperty;
import net.nemerosa.ontrack.extension.artifactory.property.ArtifactoryPromotionSyncPropertyType;
import net.nemerosa.ontrack.model.job.*;
import net.nemerosa.ontrack.model.structure.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.replace;

@Service
public class ArtifactoryPromotionSyncServiceImpl implements JobProvider {

    private final Logger logger = LoggerFactory.getLogger(ArtifactoryPromotionSyncServiceImpl.class);
    private final StructureService structureService;
    private final PropertyService propertyService;
    private final ArtifactoryClientFactory artifactoryClientFactory;

    @Autowired
    public ArtifactoryPromotionSyncServiceImpl(StructureService structureService, PropertyService propertyService, ArtifactoryClientFactory artifactoryClientFactory) {
        this.structureService = structureService;
        this.propertyService = propertyService;
        this.artifactoryClientFactory = artifactoryClientFactory;
    }

    @Override
    public Collection<Job> getJobs() {
        // For all projects...
        return structureService.getProjectList().stream()
                // ... and their branches
                .flatMap(project -> structureService.getBranchesForProject(project.getId()).stream())
                        // ... gets those with the sync. property
                .filter(branch -> propertyService.hasProperty(branch, ArtifactoryPromotionSyncPropertyType.class))
                        // ... creates the job
                .map(this::getBranchSyncJob)
                        // ... filters on null
                .filter(job -> job != null)
                        // OK
                .collect(Collectors.toList());
    }

    private Job getBranchSyncJob(Branch branch) {
        Property<ArtifactoryPromotionSyncProperty> syncProperty = propertyService.getProperty(branch, ArtifactoryPromotionSyncPropertyType.class);
        if (syncProperty.isEmpty()) {
            return null;
        } else {
            return new BranchJob(branch) {

                @Override
                public String getCategory() {
                    return "ArtifactoryPromotionSync";
                }

                @Override
                public String getId() {
                    return String.valueOf(branch.getId());
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
                public int getInterval() {
                    return syncProperty.getValue().getInterval();
                }

                @Override
                public JobTask createTask() {
                    return new RunnableJobTask(info -> sync(branch, info));
                }
            };
        }
    }

    private void sync(Branch branch, JobInfoListener info) {
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
        info.post(log);
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
            syncBuild(branch, buildName, buildNumber, client, info);
        }
    }

    protected void syncBuild(Branch branch, String artifactoryBuildName, String buildName, ArtifactoryClient client, JobInfoListener info) {
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
            info.post(log);
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

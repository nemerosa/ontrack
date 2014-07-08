package net.nemerosa.ontrack.extension.artifactory.service;

import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.extension.artifactory.client.ArtifactoryClient;
import net.nemerosa.ontrack.extension.artifactory.client.ArtifactoryClientFactory;
import net.nemerosa.ontrack.extension.artifactory.configuration.ArtifactoryConfiguration;
import net.nemerosa.ontrack.extension.artifactory.model.ArtifactoryStatus;
import net.nemerosa.ontrack.extension.artifactory.property.ArtifactoryPromotionSyncProperty;
import net.nemerosa.ontrack.extension.artifactory.property.ArtifactoryPromotionSyncPropertyType;
import net.nemerosa.ontrack.model.job.Job;
import net.nemerosa.ontrack.model.job.JobProvider;
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
            return new Job() {
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
                public Runnable createTask() {
                    return () -> sync(branch);
                }
            };
        }
    }

    private void sync(Branch branch) {
        // Gets the sync properties
        Property<ArtifactoryPromotionSyncProperty> syncProperty = propertyService.getProperty(branch, ArtifactoryPromotionSyncPropertyType.class);
        if (syncProperty.isEmpty()) {
            throw new IllegalStateException(String.format("Cannot find sync. property on branch %d", branch.id()));
        }
        String buildName = syncProperty.getValue().getBuildName();
        String buildNameFilter = syncProperty.getValue().getBuildNameFilter();
        ArtifactoryConfiguration configuration = syncProperty.getValue().getConfiguration();
        logger.info("[artifactory-sync] Sync branch {}/{} with Artifactory build {} ({})",
                branch.getProject().getName(),
                branch.getName(),
                buildName,
                buildNameFilter);
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
            syncBuild(branch, buildName, buildNumber, client);
        }
    }

    private void syncBuild(Branch branch, String artifactoryBuildName, String buildName, ArtifactoryClient client) {
        // Looks for the build
        Optional<Build> buildOpt = structureService.findBuildByName(
                branch.getProject().getName(),
                branch.getName(),
                buildName
        );
        if (buildOpt.isPresent()) {
            // Log
            logger.debug("[artifactory-sync] Sync branch {}/{} for Artifactory build {}",
                    branch.getProject().getName(),
                    branch.getName(),
                    buildName);
            // Gets the build information from Artifactory
            JsonNode buildInfo = client.getBuildInfo(artifactoryBuildName, buildName);
            // Gets the list of statuses
            List<ArtifactoryStatus> statuses = client.getStatuses(buildInfo);
        }
    }
}

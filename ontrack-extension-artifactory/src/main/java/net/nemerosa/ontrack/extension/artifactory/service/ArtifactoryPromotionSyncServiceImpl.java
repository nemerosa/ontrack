package net.nemerosa.ontrack.extension.artifactory.service;

import net.nemerosa.ontrack.extension.artifactory.client.ArtifactoryClient;
import net.nemerosa.ontrack.extension.artifactory.client.ArtifactoryClientFactory;
import net.nemerosa.ontrack.extension.artifactory.configuration.ArtifactoryConfiguration;
import net.nemerosa.ontrack.extension.artifactory.property.ArtifactoryPromotionSyncProperty;
import net.nemerosa.ontrack.extension.artifactory.property.ArtifactoryPromotionSyncPropertyType;
import net.nemerosa.ontrack.model.job.Job;
import net.nemerosa.ontrack.model.job.JobProvider;
import net.nemerosa.ontrack.model.structure.Branch;
import net.nemerosa.ontrack.model.structure.Property;
import net.nemerosa.ontrack.model.structure.PropertyService;
import net.nemerosa.ontrack.model.structure.StructureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ArtifactoryPromotionSyncServiceImpl implements JobProvider {

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
        // Gets an Artifactory client
        ArtifactoryClient client = artifactoryClientFactory.getClient(configuration);
        // Gets all the build numbers for the specified build name
        List<String> buildNumbers = client.getBuildNumbers(buildName);
        System.out.format("%d build numbers to index%n", buildNumbers.size());
        // FIXME Operates the synchronisation
    }
}

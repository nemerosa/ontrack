package net.nemerosa.ontrack.extension.artifactory.service;

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
import java.util.stream.Collectors;

@Service
public class ArtifactorySyncServiceImpl implements JobProvider {

    private final StructureService structureService;
    private final PropertyService propertyService;

    @Autowired
    public ArtifactorySyncServiceImpl(StructureService structureService, PropertyService propertyService) {
        this.structureService = structureService;
        this.propertyService = propertyService;
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
        // FIXME Operates the synchronisation
    }
}

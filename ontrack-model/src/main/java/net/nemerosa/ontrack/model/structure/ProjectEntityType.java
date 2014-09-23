package net.nemerosa.ontrack.model.structure;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Enumeration of all {@link net.nemerosa.ontrack.model.structure.ProjectEntity} types together
 * with the way to load them from the {@link net.nemerosa.ontrack.model.structure.StructureService} service.
 */
public enum ProjectEntityType {

    PROJECT("project", StructureService::getProject),

    BRANCH("branch", StructureService::getBranch),

    PROMOTION_LEVEL("promotion level", StructureService::getPromotionLevel),

    VALIDATION_STAMP("validation stamp", StructureService::getValidationStamp),

    BUILD("build", StructureService::getBuild),

    PROMOTION_RUN("promotion run", StructureService::getPromotionRun),

    VALIDATION_RUN("validation run", StructureService::getValidationRun);

    private final String displayName;
    private final BiFunction<StructureService, ID, ProjectEntity> entityFn;

    ProjectEntityType(String displayName, BiFunction<StructureService, ID, ProjectEntity> entityFn) {
        this.displayName = displayName;
        this.entityFn = entityFn;
    }

    public Function<ID, ProjectEntity> getEntityFn(StructureService structureService) {
        return id -> entityFn.apply(structureService, id);
    }

    public String getDisplayName() {
        return displayName;
    }
}

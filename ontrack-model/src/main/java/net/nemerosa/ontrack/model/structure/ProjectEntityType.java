package net.nemerosa.ontrack.model.structure;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Enumeration of all {@link net.nemerosa.ontrack.model.structure.ProjectEntity} types together
 * with the way to load them from the {@link net.nemerosa.ontrack.model.structure.StructureService} service.
 */
public enum ProjectEntityType {

    PROJECT("project", StructureService::getProject, StructureService::findProjectByID),

    BRANCH("branch", StructureService::getBranch, StructureService::findBranchByID),

    PROMOTION_LEVEL("promotion level", StructureService::getPromotionLevel, StructureService::findPromotionLevelByID),

    VALIDATION_STAMP("validation stamp", StructureService::getValidationStamp, StructureService::findValidationStampByID),

    BUILD("build", StructureService::getBuild, StructureService::findBuildByID),

    PROMOTION_RUN("promotion run", StructureService::getPromotionRun, StructureService::findPromotionRunByID),

    VALIDATION_RUN("validation run", StructureService::getValidationRun);

    private final String displayName;
    private final BiFunction<StructureService, ID, ProjectEntity> entityFn;
    private final BiFunction<StructureService, ID, ProjectEntity> findEntityFn;

    @Deprecated
    ProjectEntityType(String displayName, BiFunction<StructureService, ID, ProjectEntity> entityFn) {
        this(displayName, entityFn, entityFn);
    }

    ProjectEntityType(String displayName, BiFunction<StructureService, ID, ProjectEntity> entityFn, BiFunction<StructureService, ID, ProjectEntity> findEntityFn) {
        this.displayName = displayName;
        this.entityFn = entityFn;
        this.findEntityFn = findEntityFn;
    }

    public Function<ID, ProjectEntity> getFindEntityFn(StructureService structureService) {
        return id -> findEntityFn.apply(structureService, id);
    }

    public Function<ID, ProjectEntity> getEntityFn(StructureService structureService) {
        return id -> entityFn.apply(structureService, id);
    }

    public String getDisplayName() {
        return displayName;
    }
}

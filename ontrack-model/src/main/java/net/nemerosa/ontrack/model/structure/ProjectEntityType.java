package net.nemerosa.ontrack.model.structure;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Enumeration of all {@link net.nemerosa.ontrack.model.structure.ProjectEntity} types together
 * with the way to load them from the {@link net.nemerosa.ontrack.model.structure.StructureService} service.
 */
public enum ProjectEntityType {

    PROJECT(StructureService::getProject),

    BRANCH(StructureService::getBranch),

    PROMOTION_LEVEL(StructureService::getPromotionLevel),

    VALIDATION_STAMP(StructureService::getValidationStamp),

    BUILD(StructureService::getBuild),

    PROMOTION_RUN(StructureService::getPromotionRun),

    VALIDATION_RUN(StructureService::getValidationRun);

    private final BiFunction<StructureService, ID, ProjectEntity> entityFn;

    ProjectEntityType(BiFunction<StructureService, ID, ProjectEntity> entityFn) {
        this.entityFn = entityFn;
    }

    public Function<ID, ProjectEntity> getEntityFn(StructureService structureService) {
        return id -> entityFn.apply(structureService, id);
    }
}

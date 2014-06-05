package net.nemerosa.ontrack.model.structure;

import java.util.function.BiFunction;
import java.util.function.Function;

public enum PropertyEntity {

    PROJECT(StructureService::getProject),

    BRANCH(StructureService::getBranch),

    PROMOTION_LEVEL(StructureService::getPromotionLevel),

    VALIDATION_STAMP(StructureService::getValidationStamp),

    BUILD(StructureService::getBuild),

    VALIDATION_RUN(StructureService::getValidationRun);

    private final BiFunction<StructureService, ID, ProjectEntity> entityFn;

    PropertyEntity(BiFunction<StructureService, ID, ProjectEntity> entityFn) {
        this.entityFn = entityFn;
    }

    public Function<ID, ProjectEntity> getEntityFn(StructureService structureService) {
        return id -> entityFn.apply(structureService, id);
    }
}

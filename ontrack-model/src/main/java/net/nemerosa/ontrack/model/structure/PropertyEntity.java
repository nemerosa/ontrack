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

    private final BiFunction<StructureService, ID, Entity> entityFn;

    PropertyEntity(BiFunction<StructureService, ID, Entity> entityFn) {
        this.entityFn = entityFn;
    }

    public Function<ID, Entity> getEntityFn(StructureService structureService) {
        return id -> entityFn.apply(structureService, id);
    }
}

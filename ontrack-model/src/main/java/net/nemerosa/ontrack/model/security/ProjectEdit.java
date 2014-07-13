package net.nemerosa.ontrack.model.security;

public interface ProjectEdit extends ProjectView, ProjectConfig,
        BuildCreate,
        PromotionLevelCreate, PromotionLevelEdit, PromotionLevelDelete,
        ValidationStampCreate, ValidationStampEdit, ValidationStampDelete,
        PromotionRunCreate,
        ValidationRunCreate, ValidationRunStatusChange {
}

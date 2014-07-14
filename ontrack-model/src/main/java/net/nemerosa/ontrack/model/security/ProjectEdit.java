package net.nemerosa.ontrack.model.security;

public interface ProjectEdit extends ProjectView, ProjectConfig,
        BranchCreate, BranchEdit, BranchDelete,
        BuildCreate, BuildEdit, BuildDelete,
        PromotionLevelCreate, PromotionLevelEdit, PromotionLevelDelete,
        ValidationStampCreate, ValidationStampEdit, ValidationStampDelete,
        PromotionRunCreate,
        ValidationRunCreate, ValidationRunStatusChange {
}

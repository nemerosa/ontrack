package net.nemerosa.ontrack.model.security;

public interface ProjectEdit extends ProjectView, ProjectConfig,
        BranchCreate, BranchEdit, BranchDelete,
        BranchTemplateSync, BranchTemplateMgt,
        BuildCreate, BuildEdit, BuildDelete,
        PromotionLevelCreate, PromotionLevelEdit, PromotionLevelDelete,
        ValidationStampCreate, ValidationStampEdit, ValidationStampDelete,
        PromotionRunCreate,
        ValidationRunCreate, ValidationRunStatusChange {
}

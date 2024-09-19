package net.nemerosa.ontrack.model.security;

@CoreFunction
public interface ProjectEdit extends ProjectView, ProjectConfig, ProjectDisable,
        BranchCreate, BranchEdit, BranchDisable, BranchDelete,
        BuildCreate, BuildEdit, BuildDelete, BuildConfig,
        PromotionLevelCreate, PromotionLevelEdit, PromotionLevelDelete,
        ValidationStampCreate, ValidationStampEdit, ValidationStampDelete,
        PromotionRunCreate, PromotionRunDelete,
        ValidationRunCreate, ValidationRunStatusChange, ValidationRunStatusCommentEditOwn, ValidationRunStatusCommentEdit {
}

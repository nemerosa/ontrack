package net.nemerosa.ontrack.model.security;

@CoreFunction
public interface ProjectEdit extends ProjectView, ProjectConfig,
        BranchCreate, BranchEdit, BranchDelete,
        BuildCreate, BuildEdit, BuildDelete, BuildConfig,
        PromotionLevelCreate, PromotionLevelEdit, PromotionLevelDelete,
        ValidationStampCreate, ValidationStampEdit, ValidationStampDelete,
        PromotionRunCreate, PromotionRunDelete,
        ValidationRunCreate, ValidationRunStatusChange, ValidationRunStatusCommentEditOwn, ValidationRunStatusCommentEdit {
}

package net.nemerosa.ontrack.model.security;

public interface ProjectEdit extends ProjectView,
        BuildCreate,
        PromotionLevelCreate, PromotionLevelEdit,
        ValidationStampCreate, ValidationStampEdit,
        PromotionRunCreate,
        ValidationRunCreate, ValidationRunStatusChange {
}

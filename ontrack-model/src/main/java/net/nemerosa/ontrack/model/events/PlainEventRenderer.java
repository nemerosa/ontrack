package net.nemerosa.ontrack.model.events;

import net.nemerosa.ontrack.model.structure.*;

public class PlainEventRenderer implements EventRenderer {

    public static final EventRenderer INSTANCE = new PlainEventRenderer();

    @Override
    public String render(ProjectEntity projectEntity, Event event) {
        switch (projectEntity.getProjectEntityType()) {
            case PROJECT:
                return ((Project) projectEntity).getName();
            case BRANCH:
                return ((Branch) projectEntity).getName();
            case PROMOTION_LEVEL:
                return ((PromotionLevel) projectEntity).getName();
            case VALIDATION_STAMP:
                return ((ValidationStamp) projectEntity).getName();
            case BUILD:
                return ((Build) projectEntity).getName();
            case VALIDATION_RUN:
                return "#" + ((ValidationRun) projectEntity).getRunOrder();
            default:
                throw new EventCannotRenderEntityException(event.getTemplate(), projectEntity);
        }
    }

    @Override
    public String render(String valueKey, String value, Event event) {
        return value;
    }
}

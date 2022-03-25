package net.nemerosa.ontrack.model.events;

import net.nemerosa.ontrack.model.structure.*;
import net.nemerosa.ontrack.model.support.NameValue;

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
            case PROMOTION_RUN:
                PromotionRun promotionRun = (PromotionRun) projectEntity;
                return String.format(
                        "%s->%s",
                        promotionRun.getBuild().getName(),
                        promotionRun.getPromotionLevel().getName()
                );
            default:
                throw new EventCannotRenderEntityException(event.getEventType().getTemplate(), projectEntity);
        }
    }

    @Override
    public String render(String valueKey, NameValue value, Event event) {
        return value.getValue();
    }
}

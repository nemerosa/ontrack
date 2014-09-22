package net.nemerosa.ontrack.model.events

import net.nemerosa.ontrack.model.structure.ProjectEntity

import static net.nemerosa.ontrack.model.structure.ProjectEntityType.*

class PlainEventRenderer implements EventRenderer {

    public def static final EventRenderer INSTANCE = new PlainEventRenderer();

    @Override
    String render(ProjectEntity projectEntity, Event event) {
        switch (projectEntity.projectEntityType) {
            case PROJECT:
            case BRANCH:
            case PROMOTION_LEVEL:
            case VALIDATION_STAMP:
            case BUILD:
                return projectEntity.name
            case VALIDATION_RUN:
                return "#${projectEntity.runOrder}"
            default:
                throw new EventCannotRenderEntityException(event.template, projectEntity)
        }
    }

    @Override
    String render(String valueKey, String value, Event event) {
        value;
    }
}

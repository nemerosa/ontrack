package net.nemerosa.ontrack.model.events;

import net.nemerosa.ontrack.common.BaseException;
import net.nemerosa.ontrack.model.structure.ProjectEntity;

public class EventCannotRenderEntityException extends BaseException {
    public EventCannotRenderEntityException(String template, ProjectEntity projectEntity) {
        super("Cannot render entity %s for event: %s", projectEntity.getProjectEntityType(), template);
    }
}

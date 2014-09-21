package net.nemerosa.ontrack.model.events

import net.nemerosa.ontrack.common.BaseException
import net.nemerosa.ontrack.model.structure.ProjectEntity

class EventCannotRenderEntityException extends BaseException {
    EventCannotRenderEntityException(String template, ProjectEntity projectEntity) {
        super("Cannot render entity %s for event: %s", projectEntity.projectEntityType, template)
    }
}

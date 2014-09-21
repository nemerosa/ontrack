package net.nemerosa.ontrack.model.events;

import net.nemerosa.ontrack.common.BaseException;
import net.nemerosa.ontrack.model.structure.ProjectEntityType;

public class EventMissingEntityException extends BaseException {
    public EventMissingEntityException(String template, ProjectEntityType projectEntityType) {
        super("Event cannot be defined because entity %s is not defined in template: %s", projectEntityType, template);
    }
}

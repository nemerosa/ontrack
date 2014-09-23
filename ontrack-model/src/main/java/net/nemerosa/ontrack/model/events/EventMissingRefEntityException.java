package net.nemerosa.ontrack.model.events;

import net.nemerosa.ontrack.common.BaseException;

public class EventMissingRefEntityException extends BaseException {
    public EventMissingRefEntityException(String template) {
        super("Event cannot be defined because reference entity is not defined in template: %s", template);
    }
}

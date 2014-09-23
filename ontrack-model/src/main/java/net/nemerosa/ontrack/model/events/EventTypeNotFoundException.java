package net.nemerosa.ontrack.model.events;

import net.nemerosa.ontrack.common.BaseException;

public class EventTypeNotFoundException extends BaseException {
    public EventTypeNotFoundException(String id) {
        super("Event type not found for %s", id);
    }
}

package net.nemerosa.ontrack.model.events;

import net.nemerosa.ontrack.common.BaseException;

public class EventMissingValueException extends BaseException {
    public EventMissingValueException(String template, String valueKey) {
        super("Event cannot be defined because value with key %s is not defined in template: %s", valueKey, template);
    }
}

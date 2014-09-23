package net.nemerosa.ontrack.model.events;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SimpleEventType implements EventType {

    private final String id;
    private final String template;

    public static EventType of(String id, String template) {
        return new SimpleEventType(id, template);
    }

}

package net.nemerosa.ontrack.service.events;

import net.nemerosa.ontrack.model.events.EventFactory;
import net.nemerosa.ontrack.model.events.EventType;
import net.nemerosa.ontrack.model.events.SimpleEventType;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class EventFactoryTest {

    @Test
    public void custom_event_type() {
        EventFactory factory = new EventFactoryImpl();
        factory.register(SimpleEventType.of("custom-type", "My custom event type"));
        EventType eventType = factory.toEventType("custom-type");
        assertEquals("custom-type", eventType.getId());
        assertEquals("My custom event type", eventType.getTemplate());
    }

}

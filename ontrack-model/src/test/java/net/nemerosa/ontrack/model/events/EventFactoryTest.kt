package net.nemerosa.ontrack.model.events

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class EventFactoryTest {

    @Test
    fun custom_event_type() {
        val factory: EventFactory = EventFactoryImpl()
        factory.register(SimpleEventType.of("custom-type", "My custom event type"))
        val eventType = factory.toEventType("custom-type")
        assertEquals("custom-type", eventType.id)
        assertEquals("My custom event type", eventType.template)
    }

}

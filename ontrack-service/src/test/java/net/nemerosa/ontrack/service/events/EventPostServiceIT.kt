package net.nemerosa.ontrack.service.events

import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.it.AsAdminTest
import net.nemerosa.ontrack.model.events.*
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@AsAdminTest
class EventPostServiceIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var eventPostService: EventPostService

    @Autowired
    private lateinit var eventQueryService: EventQueryService

    @Autowired
    private lateinit var eventFactory: EventFactory

    @Test
    fun `Saving large events values`() {
        val id = uid("testing-")
        val eventType = SimpleEventType(id, "Testing event.", "Some description", emptyEventContext())
        eventFactory.register(eventType)
        project {
            val event = Event.of(eventType)
                .with(project)
                .apply {
                    repeat(200) {
                        with("name-$it", "value-$it")
                    }
                }
                .build()
            eventPostService.post(event)
            // Retrieving the event
            val saved = eventQueryService.getLastEvent(project, eventType)
            assertNotNull(saved, "Event has been saved") {
                // Gets the values
                val values = it.values.mapValues { (_, value) -> value.value }
                // Check
                assertEquals(
                    (0..199).associate { "name-$it" to "value-$it" },
                    values
                )
            }
        }
    }

}
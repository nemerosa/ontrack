package net.nemerosa.ontrack.service.events

import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.it.AsAdminTest
import net.nemerosa.ontrack.model.events.EventFactory
import net.nemerosa.ontrack.model.events.SerializableEvent
import net.nemerosa.ontrack.model.events.SerializableEventService
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import net.nemerosa.ontrack.model.structure.Signature
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

@AsAdminTest
class SerializableEventServiceIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var eventFactory: EventFactory

    @Autowired
    private lateinit var serializableEventService: SerializableEventService

    @Test
    fun dehydration() {
        project {
            branch {
                val event = eventFactory.newBranch(this)
                val dehydratedEvent = serializableEventService.dehydrate(event)
                assertEquals(
                    SerializableEvent(
                        id = 0,
                        eventType = "new_branch",
                        signature = event.signature,
                        entities = mapOf(
                            ProjectEntityType.PROJECT to project.id(),
                            ProjectEntityType.BRANCH to id(),
                        ),
                        extraEntities = emptyMap(),
                        ref = null,
                        values = emptyMap(),
                    ),
                    dehydratedEvent
                )
            }
        }
    }

    @Test
    fun hydration() {
        project {
            branch {
                val dehydrated = SerializableEvent(
                    id = 12,
                    eventType = "new_branch",
                    signature = Signature.of("test"),
                    entities = mapOf(
                        ProjectEntityType.PROJECT to project.id(),
                        ProjectEntityType.BRANCH to id(),
                    ),
                    extraEntities = emptyMap(),
                    ref = null,
                    values = emptyMap(),
                )
                val event = serializableEventService.hydrate(dehydrated)
                assertEquals(12, event.id)
                assertEquals(EventFactory.NEW_BRANCH, event.eventType)
                assertEquals(dehydrated.signature, event.signature)
                assertEquals(
                    mapOf(
                        ProjectEntityType.PROJECT to project,
                        ProjectEntityType.BRANCH to this,
                    ),
                    event.entities
                )
            }
        }
    }

}
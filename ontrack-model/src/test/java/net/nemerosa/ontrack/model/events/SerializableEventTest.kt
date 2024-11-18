package net.nemerosa.ontrack.model.events

import net.nemerosa.ontrack.model.structure.ProjectEntityType
import net.nemerosa.ontrack.model.structure.ProjectFixtures
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class SerializableEventTest {

    @Test
    fun `Find value`() {
        val event = MockEventType.serializedMockEvent("Some text")
        assertEquals(
            null,
            event.findValue("xxx")
        )
        assertEquals(
            "Some text",
            event.findValue(MockEventType.EVENT_MOCK)
        )
    }

    @Test
    fun `With value`() {
        val event = MockEventType.serializedMockEvent("Some text")
            .withValue("test", "Some value")
        assertEquals(
            "Some value",
            event.findValue("test")
        )
    }

    @Test
    fun `With entity id`() {
        val project = ProjectFixtures.testProject()
        val event = MockEventType.serializedMockEvent("Some text")
            .withEntity(project)
        assertEquals(
            project.id(),
            event.findEntityId(ProjectEntityType.PROJECT)
        )
    }

}
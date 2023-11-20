package net.nemerosa.ontrack.boot.ui

import io.mockk.every
import io.mockk.mockk
import net.nemerosa.ontrack.extension.api.support.TestExtensionFeature
import net.nemerosa.ontrack.extension.api.support.TestSimpleProperty
import net.nemerosa.ontrack.extension.api.support.TestSimplePropertyType
import net.nemerosa.ontrack.model.events.Event
import net.nemerosa.ontrack.model.events.EventFactory
import net.nemerosa.ontrack.model.events.HtmlNotificationEventRenderer
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import net.nemerosa.ontrack.model.structure.ProjectFixtures
import net.nemerosa.ontrack.model.structure.PropertyService
import net.nemerosa.ontrack.model.structure.PropertyTypeDescriptor
import net.nemerosa.ontrack.model.structure.Signature
import net.nemerosa.ontrack.model.support.NameValue
import net.nemerosa.ontrack.test.assertIs
import org.junit.jupiter.api.Test

class EventControllerTest {

    @Test
    fun `Event property values are converted into property descriptions`() {
        val propertyService = mockk<PropertyService>()
        every {
            propertyService.getPropertyTypeByName<TestSimpleProperty>(TestSimplePropertyType::class.java.name)
        } returns TestSimplePropertyType(TestExtensionFeature())

        val htmlNotificationEventRenderer = mockk<HtmlNotificationEventRenderer>(relaxed = true)

        val controller = EventController(
            mockk(),
            propertyService,
            htmlNotificationEventRenderer
        )

        val testProject = ProjectFixtures.testProject()
        val event = Event(
            eventType = EventFactory.PROPERTY_CHANGE,
            signature = Signature.of("test"),
            entities = mapOf(
                ProjectEntityType.PROJECT to testProject,
            ),
            extraEntities = emptyMap(),
            ref = ProjectEntityType.PROJECT,
            values = mapOf(
                "property" to NameValue(
                    TestSimplePropertyType::class.java.name,
                    "Release"
                ),
                "entity" to NameValue(
                    "project",
                    "source"
                )
            )
        )

        val ui = controller.toUIEvent(event)

        val propertyData = ui.data.get("property")
        assertIs<PropertyTypeDescriptor>(propertyData) {}
    }

}

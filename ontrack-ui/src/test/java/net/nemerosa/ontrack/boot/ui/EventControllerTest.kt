package net.nemerosa.ontrack.boot.ui

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import net.nemerosa.ontrack.extension.api.support.TestExtensionFeature
import net.nemerosa.ontrack.extension.api.support.TestSimpleProperty
import net.nemerosa.ontrack.extension.api.support.TestSimplePropertyType
import net.nemerosa.ontrack.model.events.Event
import net.nemerosa.ontrack.model.events.EventFactory
import net.nemerosa.ontrack.model.structure.PropertyService
import net.nemerosa.ontrack.model.structure.PropertyTypeDescriptor
import net.nemerosa.ontrack.model.structure.Signature
import net.nemerosa.ontrack.model.support.NameValue
import net.nemerosa.ontrack.test.assertIs
import org.junit.Test

class EventControllerTest {

    @Test
    fun `Event property values are converted into property descriptions`() {
        val propertyService = mock<PropertyService>()
        whenever(propertyService.getPropertyTypeByName<TestSimpleProperty>(TestSimplePropertyType::class.java.name)).thenReturn(
                TestSimplePropertyType(TestExtensionFeature())
        )

        val controller = EventController(
                mock(),
                propertyService
        )

        val event = Event(
                EventFactory.PROPERTY_CHANGE,
                Signature.of("test"),
                emptyMap(),
                null,
                mapOf(
                        "property" to NameValue(
                                TestSimplePropertyType::class.java.name,
                                "Release"
                        )
                )
        )

        val ui = controller.toUIEvent(event)

        val propertyData = ui.data.get("property")
        assertIs<PropertyTypeDescriptor>(propertyData) {}
    }

}

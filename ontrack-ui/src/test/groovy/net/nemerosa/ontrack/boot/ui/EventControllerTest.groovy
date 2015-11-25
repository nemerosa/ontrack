package net.nemerosa.ontrack.boot.ui

import net.nemerosa.ontrack.extension.general.GeneralExtensionFeature
import net.nemerosa.ontrack.extension.general.ReleasePropertyType
import net.nemerosa.ontrack.model.events.Event
import net.nemerosa.ontrack.model.events.EventFactory
import net.nemerosa.ontrack.model.events.EventQueryService
import net.nemerosa.ontrack.model.structure.PropertyService
import net.nemerosa.ontrack.model.structure.PropertyTypeDescriptor
import net.nemerosa.ontrack.model.structure.Signature
import net.nemerosa.ontrack.model.support.NameValue
import org.junit.Test

import static org.junit.Assert.assertNotNull
import static org.junit.Assert.assertTrue
import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

class EventControllerTest {

    @Test
    void 'Event property values are converted into property descriptions'() {
        PropertyService propertyService = mock(PropertyService.class);
        when(propertyService.getPropertyTypeByName(ReleasePropertyType.class.getName())).thenReturn(
                new ReleasePropertyType(
                        new GeneralExtensionFeature()
                )
        );

        EventController controller = new EventController(
                mock(EventQueryService.class),
                propertyService
        );

        Event event = new Event(
                EventFactory.PROPERTY_CHANGE,
                Signature.of("test"),
                Collections.emptyMap(),
                null,
                Collections.singletonMap(
                        "property",
                        new NameValue(
                                ReleasePropertyType.class.getName(),
                                "Release"
                        )
                )
        );

        UIEvent ui = controller.toUIEvent(event);

        Object propertyData = ui.getData().get("property");
        assertNotNull(propertyData);
        assertTrue(propertyData instanceof PropertyTypeDescriptor);
    }

}

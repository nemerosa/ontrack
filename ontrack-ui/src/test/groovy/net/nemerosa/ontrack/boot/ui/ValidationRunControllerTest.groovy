package net.nemerosa.ontrack.boot.ui

import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.ui.controller.AbstractResourceController
import net.nemerosa.ontrack.ui.controller.MockURIBuilder
import net.nemerosa.ontrack.ui.resource.Resources
import org.junit.Before
import org.junit.Test

import java.lang.reflect.Field

import static org.mockito.Mockito.mock
import static org.mockito.Mockito.when

class ValidationRunControllerTest {

    private ValidationRunController controller
    private StructureService structureService

    @Before
    void before() {
        structureService = mock(StructureService.class)
        ValidationRunStatusService validationRunStatusService = mock(ValidationRunStatusService.class)
        PropertyService propertyService = mock(PropertyService.class)
        SecurityService securityService = mock(SecurityService.class)
        controller = new ValidationRunController(
                structureService,
                validationRunStatusService,
                propertyService,
                securityService
        )
        // Mock URI builder for tests
        Field field = AbstractResourceController.class.getDeclaredField("uriBuilder")
        field.setAccessible(true)
        field.set(controller, new MockURIBuilder())
    }

    @Test
    void getValidationRunsForValidationStamp_none() throws Exception {
        when(structureService.getValidationRunsForValidationStamp(ID.of(1), 0, 10)).thenReturn(Collections.emptyList())
        Resources<ValidationRun> resources = controller.getValidationRunsForValidationStamp(ID.of(1), 0, 10)
        assert resources != null
        assert resources.getPagination() != null
        assert resources.getPagination().getPrev() == null
        assert resources.getPagination().getNext() == null
        assert resources.getPagination().getOffset() == 0
        assert resources.getPagination().getLimit() == 10
        assert resources.getPagination().getTotal() == 0
        assert resources.getResources().isEmpty()
    }
}
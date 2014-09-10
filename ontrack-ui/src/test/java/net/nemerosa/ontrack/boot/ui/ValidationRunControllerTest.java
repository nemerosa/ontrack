package net.nemerosa.ontrack.boot.ui;

import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.*;
import net.nemerosa.ontrack.ui.controller.AbstractResourceController;
import net.nemerosa.ontrack.ui.controller.MockURIBuilder;
import net.nemerosa.ontrack.ui.resource.Resources;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.Collections;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ValidationRunControllerTest {

    private ValidationRunController controller;
    private StructureService structureService;

    @Before
    public void before() throws IllegalAccessException, NoSuchFieldException {
        structureService = mock(StructureService.class);
        ValidationRunStatusService validationRunStatusService = mock(ValidationRunStatusService.class);
        PropertyService propertyService = mock(PropertyService.class);
        SecurityService securityService = mock(SecurityService.class);
        controller = new ValidationRunController(
                structureService,
                validationRunStatusService,
                propertyService,
                securityService
        );
        // Mock URI builder for tests
        Field field = AbstractResourceController.class.getDeclaredField("uriBuilder");
        field.setAccessible(true);
        field.set(controller, new MockURIBuilder());
    }

    @Test
    public void getValidationRunsForValidationStamp_none() throws Exception {
        when(structureService.getValidationRunsForValidationStamp(ID.of(1), 0, 10)).thenReturn(Collections.emptyList());
        Resources<ValidationRun> resources = controller.getValidationRunsForValidationStamp(ID.of(1), 0, 10);
        assertNotNull(resources);
        assertNotNull(resources.getPagination());
        assertNull(resources.getPagination().getPrev());
        assertNull(resources.getPagination().getNext());
        assertEquals(resources.getPagination().getOffset(), 0);
        assertEquals(resources.getPagination().getLimit(), 10);
        assertEquals(resources.getPagination().getTotal(), 0);
        assertTrue(resources.getResources().isEmpty());
    }
}
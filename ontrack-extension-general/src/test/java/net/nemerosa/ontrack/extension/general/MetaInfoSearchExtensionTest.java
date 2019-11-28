package net.nemerosa.ontrack.extension.general;

import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.PropertyService;
import net.nemerosa.ontrack.model.structure.StructureService;
import net.nemerosa.ontrack.ui.controller.MockURIBuilder;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class MetaInfoSearchExtensionTest {

    private MetaInfoSearchExtension extension;

    @Before
    public void before() {
        PropertyService propertyService = mock(PropertyService.class);
        StructureService structureService = mock(StructureService.class);
        extension = new MetaInfoSearchExtension(
                new GeneralExtensionFeature(),
                new MockURIBuilder(),
                propertyService,
                structureService,
                mock(SecurityService.class)
        );
    }

    @Test
    public void isTokenSearchable_name_and_value() {
        assertTrue(extension.isTokenSearchable("name:value"));
    }

    @Test
    public void isTokenSearchable_name_and_wildcard() {
        assertTrue(extension.isTokenSearchable("name:value*"));
        assertTrue(extension.isTokenSearchable("name:*"));
    }

    @Test
    public void isTokenSearchable_name_only() {
        assertTrue(extension.isTokenSearchable("name:"));
    }

    @Test
    public void isTokenSearchable_any() {
        assertFalse(extension.isTokenSearchable("name"));
    }

}

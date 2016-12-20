package net.nemerosa.ontrack.service;

import net.nemerosa.ontrack.model.buildfilter.BuildFilter;
import net.nemerosa.ontrack.model.buildfilter.BuildFilterService;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.PropertyService;
import net.nemerosa.ontrack.model.structure.StructureService;
import net.nemerosa.ontrack.repository.BuildFilterRepository;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class BuildFilterServiceImplTest {

    private BuildFilterService service;

    @Before
    public void before() {
        BuildFilterRepository buildFilterRepository = mock(BuildFilterRepository.class);
        StructureService structureService = mock(StructureService.class);
        SecurityService securityService = mock(SecurityService.class);
        PropertyService propertyService = mock(PropertyService.class);
        service = new BuildFilterServiceImpl(
                Collections.emptyList(),
                buildFilterRepository,
                structureService,
                securityService,
                propertyService);
    }

    @Test
    public void standardFilter() {
        BuildFilter filter = service.standardFilter(20)
                .withWithPromotionLevel("BRONZE")
                .withWithProperty("my.property.MyPropertyType")
                .withWithPropertyValue("Value")
                .build();
        assertTrue(filter instanceof StandardBuildFilter);
        StandardBuildFilter std = (StandardBuildFilter) filter;
        assertEquals(20, std.getData().getCount());
        assertEquals("BRONZE", std.getData().getWithPromotionLevel());
        assertEquals("my.property.MyPropertyType", std.getData().getWithProperty());
        assertEquals("Value", std.getData().getWithPropertyValue());
    }

}

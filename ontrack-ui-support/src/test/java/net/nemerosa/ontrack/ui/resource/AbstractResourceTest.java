package net.nemerosa.ontrack.ui.resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.ui.controller.MockURIBuilder;
import org.junit.Before;
import org.mockito.Mockito;

import java.util.Collections;

import static org.junit.Assert.assertEquals;

public abstract class AbstractResourceTest {

    protected ResourceObjectMapper mapper;
    protected SecurityService securityService;

    @Before
    public void before() {
        securityService = Mockito.mock(SecurityService.class);
        mapper = new ResourceObjectMapperFactory().resourceObjectMapper(
                Collections.emptyList(),
                new DefaultResourceContext(new MockURIBuilder(), securityService)
        );
    }

    public static void assertResourceJson(ResourceObjectMapper mapper, JsonNode expectedJson, Object o) throws JsonProcessingException {
        assertEquals(
                mapper.getObjectMapper().writeValueAsString(expectedJson),
                mapper.write(o)
        );
    }

    public static void assertResourceJson(ResourceObjectMapper mapper, JsonNode expectedJson, Object o, Class<?> view) throws JsonProcessingException {
        assertEquals(
                mapper.getObjectMapper().writeValueAsString(expectedJson),
                mapper.write(o, view)
        );
    }
}

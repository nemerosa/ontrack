package net.nemerosa.ontrack.ui.resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Test;

import java.net.URI;

import static net.nemerosa.ontrack.json.JsonUtils.object;
import static org.junit.Assert.assertEquals;

public class ResourceTest extends AbstractResourceTest {

    @Test
    public void resource_to_json() throws JsonProcessingException {
        Dummy info = new Dummy("1.0.0");
        Resource<Dummy> resource = Resource.of(info, URI.create("http://host/dummy")).with("connectors", URI.create("http://host/dummy/test"));
        assertResourceJson(
                mapper,
                object()
                        .with("_self", "http://host/dummy")
                        .with("version", "1.0.0")
                        .with("connectors", "http://host/dummy/test")
                        .end(),
                resource
        );
    }

    @Test(expected = NullPointerException.class)
    public void resource_not_null() {
        Resource.<String>of(null, URI.create(""));
    }

    @Test
    public void container_first() {
        assertEquals(String.class, Resource.of("Test", URI.create("")).getViewType());
    }

}

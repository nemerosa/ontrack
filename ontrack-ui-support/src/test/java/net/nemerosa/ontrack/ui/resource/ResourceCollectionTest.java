package net.nemerosa.ontrack.ui.resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import net.nemerosa.ontrack.json.ObjectMapperFactory;
import net.nemerosa.ontrack.test.TestUtils;
import org.junit.Test;

import java.net.URI;
import java.util.Arrays;

import static net.nemerosa.ontrack.json.JsonUtils.array;
import static net.nemerosa.ontrack.json.JsonUtils.object;

public class ResourceCollectionTest {

    @Test
    public void to_json() throws JsonProcessingException {
        ResourceCollection<Dummy> collection = ResourceCollection.of(
                Arrays.asList(
                        Resource.of(new Dummy("1"), URI.create("http://host/dummy/1")),
                        Resource.of(new Dummy("2"), URI.create("http://host/dummy/2"))
                ),
                URI.create("http://host/dummy")
        );
        TestUtils.assertJsonEquals(
                object()
                        .with("href", "http://host/dummy")
                        .with("resources", array()
                                .with(object()
                                        .with("version", "1")
                                        .with("href", "http://host/dummy/1")
                                        .end())
                                .with(object()
                                        .with("version", "2")
                                        .with("href", "http://host/dummy/2")
                                        .end())
                                .end())
                        .end(),
                ObjectMapperFactory.create().valueToTree(collection)
        );
    }

}

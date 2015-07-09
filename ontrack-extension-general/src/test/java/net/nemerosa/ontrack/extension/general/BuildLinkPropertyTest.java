package net.nemerosa.ontrack.extension.general;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Test;

import java.util.Arrays;

import static net.nemerosa.ontrack.json.JsonUtils.array;
import static net.nemerosa.ontrack.json.JsonUtils.object;
import static net.nemerosa.ontrack.test.TestUtils.assertJsonRead;

public class BuildLinkPropertyTest {

    @Test
    public void json_deserialise() throws JsonProcessingException {
        assertJsonRead(
                new BuildLinkProperty(
                        Arrays.asList(
                                new BuildLinkPropertyItem("P1", "1"),
                                new BuildLinkPropertyItem("P2", "2")
                        )
                ),
                object()
                        .with("links", array()
                                .with(object().with("project", "P1").with("build", "1").end())
                                .with(object().with("project", "P2").with("build", "2").end())
                                .end())
                        .end(),
                BuildLinkProperty.class
        );
    }

}

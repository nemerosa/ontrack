package net.nemerosa.ontrack.model.structure;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static net.nemerosa.ontrack.json.JsonUtils.array;
import static net.nemerosa.ontrack.json.JsonUtils.object;
import static net.nemerosa.ontrack.test.TestUtils.assertJsonRead;

public class BuildRequestTest {

    @Test
    public void from_json_without_properties() throws JsonProcessingException {
        assertJsonRead(
                new BuildRequest(
                        "12",
                        "Build 12",
                        Collections.emptyList()
                ),
                object()
                        .with("name", "12")
                        .with("description", "Build 12")
                        .end(),
                BuildRequest.class
        );
    }

    @Test
    public void from_json_with_properties() throws JsonProcessingException {
        assertJsonRead(
                new BuildRequest(
                        "12",
                        "Build 12",
                        Arrays.asList(
                                new PropertyCreationRequest(
                                        "build",
                                        object().with("url", "http://ci/build/12").end()
                                )
                        )
                ),
                object()
                        .with("name", "12")
                        .with("description", "Build 12")
                        .with("properties", array()
                                        .with(object()
                                                        .with("propertyTypeName", "build")
                                                        .with("propertyData", object().with("url", "http://ci/build/12").end())
                                                        .end()
                                        )
                                        .end()
                        )
                        .end(),
                BuildRequest.class
        );
    }

}

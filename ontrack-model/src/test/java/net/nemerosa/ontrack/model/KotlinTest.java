package net.nemerosa.ontrack.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Test;

import static net.nemerosa.ontrack.json.JsonUtils.object;
import static net.nemerosa.ontrack.test.TestUtils.assertJsonRead;
import static net.nemerosa.ontrack.test.TestUtils.assertJsonWrite;

public class KotlinTest {

    @Test
    public void java_to_json() throws JsonProcessingException {
        assertJsonWrite(
                object()
                        .with("name", "Name")
                        .with("value", 10)
                        .with("doubleValue", 20)
                        .end(),
                new JavaPOJO("Name", 10)
        );
    }

    @Test
    public void json_to_java() throws JsonProcessingException {
        assertJsonRead(
                new JavaPOJO("Name", 10),
                object()
                        .with("name", "Name")
                        .with("value", 10)
                        .with("doubleValue", 20)
                        .end(),
                JavaPOJO.class
        );
    }

    @Test
    public void kotlin_to_json() throws JsonProcessingException {
        assertJsonWrite(
                object()
                        .with("name", "Name")
                        .with("value", 10)
                        .with("doubleValue", 20)
                        .end(),
                new KotlinPOJO("Name", 10)
        );
    }

    @Test
    public void json_to_kotlin() throws JsonProcessingException {
        assertJsonRead(
                new KotlinPOJO("Name", 10),
                object()
                        .with("name", "Name")
                        .with("value", 10)
                        .with("doubleValue", 20)
                        .end(),
                KotlinPOJO.class
        );
    }

}

package net.nemerosa.ontrack.model.form;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Test;

import static net.nemerosa.ontrack.json.JsonUtils.object;
import static net.nemerosa.ontrack.test.TestUtils.assertJsonWrite;

public class YesNoTest {

    @Test
    public void visibleIf_present_if_not_null() throws JsonProcessingException {
        assertJsonWrite(
                object()
                        .with("type", "yesno")
                        .with("name", "test")
                        .with("label", "Test")
                        .with("required", true)
                        .with("readOnly", false)
                        .withNull("validation")
                        .with("help", "")
                        .with("visibleIf", "enabled")
                        .withNull("value")
                        .end(),
                YesNo.of("test").label("Test").visibleIf("enabled")
        );
    }

    @Test
    public void visibleIf_not_present_if_null() throws JsonProcessingException {
        assertJsonWrite(
                object()
                        .with("type", "yesno")
                        .with("name", "test")
                        .with("label", "Test")
                        .with("required", true)
                        .with("readOnly", false)
                        .withNull("validation")
                        .with("help", "")
                        .withNull("value")
                        .end(),
                YesNo.of("test").label("Test")
        );
    }

}
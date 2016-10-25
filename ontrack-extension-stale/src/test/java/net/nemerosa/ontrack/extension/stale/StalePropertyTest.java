package net.nemerosa.ontrack.extension.stale;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import net.nemerosa.ontrack.json.JsonUtils;
import net.nemerosa.ontrack.test.TestUtils;
import org.junit.Test;

public class StalePropertyTest {

    @Test
    public void backward_compatibility_of_json() throws JsonProcessingException {
        ObjectNode json = JsonUtils.object()
                .with("disablingDuration", 30)
                .with("deletingDuration", 0)
                .end();
        TestUtils.assertJsonRead(
                new StaleProperty(30, 0, null),
                json,
                StaleProperty.class
        );
    }

}

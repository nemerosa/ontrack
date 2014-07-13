package net.nemerosa.ontrack.acceptance;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class ACCGeneral extends AcceptanceTestClient {

    @Test
    public void info_api() {
        anonymous().get("info").withNode(node -> {
            JsonNode version = node.path("version");
            assertTrue(StringUtils.isNotBlank(version.path("full").asText()));
            assertTrue(StringUtils.isNotBlank(version.path("base").asText()));
            assertTrue(StringUtils.isNotBlank(version.path("build").asText()));
            assertTrue(StringUtils.isNotBlank(version.path("commit").asText()));
            assertTrue(StringUtils.isNotBlank(version.path("source").asText()));
        });
    }

}

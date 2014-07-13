package net.nemerosa.ontrack.acceptance;

import com.fasterxml.jackson.databind.JsonNode;
import net.nemerosa.ontrack.client.ClientForbiddenException;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ACCGeneral extends AcceptanceSupport {

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

    /**
     * Checks that the client received a HTTP 403 when he tries to access an non authorized resource.
     */
    @Test
    public void access_control() {
        try {
            anonymous().get("admin/logs");
            fail("The access should have been denied.");
        } catch (ClientForbiddenException ex) {
            // OK
        }
    }

}

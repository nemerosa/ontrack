package net.nemerosa.ontrack.acceptance;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class ACCGeneral extends AcceptanceSupport {

    @Test
    public void info_api() {
        anonymous().get("info").with(node -> {
            assertTrue(StringUtils.isNotBlank(node.path("version").asText()));
        });
    }

}

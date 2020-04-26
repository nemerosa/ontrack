package net.nemerosa.ontrack.acceptance.tests.dsl

import net.nemerosa.ontrack.acceptance.AbstractACCDSL
import net.nemerosa.ontrack.client.ClientCannotLoginException
import org.apache.commons.lang3.StringUtils
import org.junit.Test

import static org.junit.Assert.assertTrue
import static org.junit.Assert.fail

class ACCGeneral extends AbstractACCDSL {

    @Test
    void info_api() {
        def node = ontrack.get("rest/info")
        def version = node.version
        assertTrue(StringUtils.isNotBlank(version.full))
    }

    /**
     * Checks that the client received a HTTP 403 when he tries to access an non authorized resource.
     */
    @Test
    void access_control() {
        try {
            anonymous().get("rest/admin/logs")
            fail("The access should have been denied.")
        } catch (ClientCannotLoginException ignored) {
            // OK
        }
    }

}

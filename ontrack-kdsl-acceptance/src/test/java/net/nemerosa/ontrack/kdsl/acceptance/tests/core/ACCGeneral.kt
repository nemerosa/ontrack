package net.nemerosa.ontrack.kdsl.acceptance.tests.core

import net.nemerosa.ontrack.kdsl.acceptance.tests.AbstractACCDSLTestSupport
import org.junit.jupiter.api.Test
import org.springframework.web.client.HttpClientErrorException
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class ACCGeneral : AbstractACCDSLTestSupport() {

    @Test
    fun info_api() {
        val info = ontrack.connector.get("/rest/info").body.asJson()
        assertTrue(
                info.path("version").path("display").asText().isNotBlank(),
                "Version is filled in"
        )
    }

    /**
     * Checks that the client received a HTTP 403 when he tries to access a non-authorized resource.
     */
    @Test
    fun access_control() {
        assertFailsWith<HttpClientErrorException.Unauthorized> {
            ontrack.connector.get("/rest/admin/logs", noAuth = true)
        }
    }

}

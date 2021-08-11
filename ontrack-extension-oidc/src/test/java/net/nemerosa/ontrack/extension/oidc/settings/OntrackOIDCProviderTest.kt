package net.nemerosa.ontrack.extension.oidc.settings

import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parse
import org.junit.Test
import kotlin.test.assertEquals

class OntrackOIDCProviderTest {

    @Test
    fun `Introduction of the forceHttps flag`() {
        val provider = mapOf(
            "id" to "test",
            "name" to "Name",
            "description" to "My provider",
            "issuerId" to "issuer",
            "clientId" to "client_id",
            "clientSecret" to "client_secret",
            "groupFilter" to "ontrack.*",
        ).asJson().parse<OntrackOIDCProvider>()
        assertEquals(false, provider.forceHttps)
    }

}
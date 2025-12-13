package net.nemerosa.ontrack.extension.notifications.webhooks.auth

import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parse
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class BearerWebhookAuthenticatorConfigTest {

    @Test
    fun `Parsing without a token`() {
        assertEquals(
            emptyMap<String, String>().asJson().parse(),
            BearerWebhookAuthenticatorConfig(
                token = "",
            )
        )
    }

}
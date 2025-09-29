package net.nemerosa.ontrack.extension.notifications.webhooks.auth

import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parse
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class BasicWebhookAuthenticatorConfigTest {

    @Test
    fun `Parsing without a password`() {
        assertEquals(
            mapOf(
                "username" to "test",
            ).asJson().parse(),
            BasicWebhookAuthenticatorConfig(
                username = "test",
                password = "",
            )
        )
    }

}
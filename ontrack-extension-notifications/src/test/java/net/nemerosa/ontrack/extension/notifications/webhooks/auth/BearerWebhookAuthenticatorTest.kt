package net.nemerosa.ontrack.extension.notifications.webhooks.auth

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class BearerWebhookAuthenticatorTest {

    @Test
    fun `Password is obfuscated`() {
        val authenticator = BearerWebhookAuthenticator()
        val config = authenticator.obfuscate(
            BearerWebhookAuthenticatorConfig(
                token = "secret",
            )
        )
        assertEquals("", config.token)
    }

    @Test
    fun `Merging a blank token`() {
        val authenticator = BearerWebhookAuthenticator()
        val config = authenticator.merge(
            BearerWebhookAuthenticatorConfig(
                token = "",
            ),
            BearerWebhookAuthenticatorConfig(
                token = "new-token",
            )
        )
        assertEquals("new-token", config.token)
    }

}
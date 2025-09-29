package net.nemerosa.ontrack.extension.notifications.webhooks.auth

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class HeaderWebhookAuthenticatorTest {

    @Test
    fun `Value is obfuscated`() {
        val authenticator = HeaderWebhookAuthenticator()
        val config = authenticator.obfuscate(
            HeaderWebhookAuthenticatorConfig(
                name = "X-Ontrack-Token",
                value = "secret",
            )
        )
        assertEquals("X-Ontrack-Token", config.name)
        assertEquals("", config.value)
    }

    @Test
    fun `Merging a blank value`() {
        val authenticator = HeaderWebhookAuthenticator()
        val config = authenticator.merge(
            HeaderWebhookAuthenticatorConfig(
                name = "X-Ontrack-Token",
                value = "",
            ),
            HeaderWebhookAuthenticatorConfig(
                name = "X-Ontrack-Token",
                value = "xxxx",
            )
        )
        assertEquals("X-Ontrack-Token", config.name)
        assertEquals("xxxx", config.value)
    }

}
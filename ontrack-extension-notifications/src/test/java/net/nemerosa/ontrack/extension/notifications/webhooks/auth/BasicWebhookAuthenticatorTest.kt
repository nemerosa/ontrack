package net.nemerosa.ontrack.extension.notifications.webhooks.auth

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class BasicWebhookAuthenticatorTest {

    @Test
    fun `Password is obfuscated`() {
        val authenticator = BasicWebhookAuthenticator()
        val config = authenticator.obfuscate(
            BasicWebhookAuthenticatorConfig(
                username = "user",
                password = "password",
            )
        )
        assertEquals("user", config.username)
        assertEquals("", config.password)
    }

    @Test
    fun `Merging a blank password`() {
        val authenticator = BasicWebhookAuthenticator()
        val config = authenticator.merge(
            BasicWebhookAuthenticatorConfig(
                username = "new-user",
                password = "",
            ),
            BasicWebhookAuthenticatorConfig(
                username = "user",
                password = "xxxx",
            )
        )
        assertEquals("new-user", config.username)
        assertEquals("xxxx", config.password)
    }

}
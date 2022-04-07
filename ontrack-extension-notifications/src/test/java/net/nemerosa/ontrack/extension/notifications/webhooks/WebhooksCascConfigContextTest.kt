package net.nemerosa.ontrack.extension.notifications.webhooks

import net.nemerosa.ontrack.extension.casc.schema.CascObject
import net.nemerosa.ontrack.extension.casc.schema.cascObject
import net.nemerosa.ontrack.test.assertIs
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

internal class WebhooksCascConfigContextTest {

    @Test
    fun `Checking the Webhook Casc type`() {
        val type = cascObject(Webhook::class)
        assertIs<CascObject>(type) { webhook ->
            assertNotNull(webhook.fields.find { it.name == "authentication" }) { authenticationField ->
                assertTrue(authenticationField.required, "Authentication field is required")
                assertIs<CascObject>(authenticationField.type) { authentication ->
                    assertNotNull(authentication.fields.find { it.name == "config" }) { configField ->
                        assertEquals("JSON", configField.type.__type)
                    }
                }
            }
        }
    }

}
package net.nemerosa.ontrack.extension.notifications.webhooks

import net.nemerosa.ontrack.extension.casc.AbstractCascTestSupport
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parseAsJson
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class WebhookSettingsCascIT : AbstractCascTestSupport() {

    @Autowired
    private lateinit var webhookSettingsCascContext: WebhookSettingsCascContext

    @Test
    fun `CasC schema type`() {
        val type = webhookSettingsCascContext.jsonType
        assertEquals(
            """
                {
                  "title": "WebhookSettings",
                  "description": null,
                  "properties": {
                    "deliveriesRetentionDays": {
                      "description": "Retention time (in days) for the archiving of webhook deliveries",
                      "type": "integer"
                    },
                    "enabled": {
                      "description": "Are webhooks enabled?",
                      "type": "boolean"
                    },
                    "timeoutMinutes": {
                      "description": "Global timeout (in minutes) for all webhooks",
                      "type": "integer"
                    }
                  },
                  "required": [],
                  "additionalProperties": false,
                  "type": "object"
                }
            """.trimIndent().parseAsJson(),
            type.asJson()
        )
    }

    @Test
    fun `Webhooks settings using CasC`() {
        withCleanSettings<WebhookSettings> {
            casc(
                """
                ontrack:
                    config:
                        settings:
                            webhooks:
                                enabled: true
            """.trimIndent()
            )
            val settings = settingsService.getCachedSettings(WebhookSettings::class.java)
            assertTrue(settings.enabled)
        }
    }

}
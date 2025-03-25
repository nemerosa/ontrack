package net.nemerosa.ontrack.extension.slack

import net.nemerosa.ontrack.extension.casc.AbstractCascTestSupport
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parseAsJson
import net.nemerosa.ontrack.model.json.schema.JsonTypeBuilder
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SlackSettingsCascIT : AbstractCascTestSupport() {

    @Autowired
    private lateinit var slackSettingsCascContext: SlackSettingsCascContext

    @Autowired
    private lateinit var jsonTypeBuilder: JsonTypeBuilder

    @Test
    fun `CasC schema type`() {
        val type = slackSettingsCascContext.jsonType(jsonTypeBuilder)
        assertEquals(
            """
                {
                  "title": "SlackSettings",
                  "description": null,
                  "properties": {
                    "emoji": {
                      "description": "Emoji (like :ontrack:) to use for the message",
                      "type": "string"
                    },
                    "enabled": {
                      "description": "Is Slack communication enabled?",
                      "type": "boolean"
                    },
                    "endpoint": {
                      "description": "Slack API endpoint (leave blank for default)",
                      "type": "string"
                    },
                    "token": {
                      "description": "Slack token",
                      "type": "string"
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
    fun `Slack settings using CasC`() {
        withCleanSettings<SlackSettings> {
            casc(
                """
                ontrack:
                    config:
                        settings:
                            slack:
                                enabled: true
                                token: some-token
                                emoji: ":ontrack:"
            """.trimIndent()
            )
            val settings = settingsService.getCachedSettings(SlackSettings::class.java)
            assertTrue(settings.enabled)
            assertEquals("some-token", settings.token)
            assertEquals(":ontrack:", settings.emoji)
        }
    }

}
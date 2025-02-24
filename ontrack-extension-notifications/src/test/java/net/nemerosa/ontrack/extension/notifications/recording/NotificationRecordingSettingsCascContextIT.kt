package net.nemerosa.ontrack.extension.notifications.recording

import net.nemerosa.ontrack.extension.casc.AbstractCascTestSupport
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parseAsJson
import net.nemerosa.ontrack.model.json.schema.JsonTypeBuilder
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class NotificationRecordingSettingsCascContextIT : AbstractCascTestSupport() {

    @Autowired
    private lateinit var notificationRecordingSettingsCascContext: NotificationRecordingSettingsCascContext

    @Autowired
    private lateinit var jsonTypeBuilder: JsonTypeBuilder

    @Test
    fun `CasC schema type`() {
        val type = notificationRecordingSettingsCascContext.jsonType(jsonTypeBuilder)
        assertEquals(
            """
                {
                  "title": "NotificationRecordingSettings",
                  "description": null,
                  "properties": {
                    "cleanupIntervalSeconds": {
                      "description": "Interval between each cleanup of the recordings",
                      "type": "integer"
                    },
                    "enabled": {
                      "description": "Is the recording of notifications enabled?",
                      "type": "boolean"
                    },
                    "retentionSeconds": {
                      "description": "Number of seconds to keep the recordings",
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
    fun `Notification recording settings using CasC`() {
        withCleanSettings<NotificationRecordingSettings> {
            casc(
                """
                    ontrack:
                        config:
                            settings:
                                notification-recordings:
                                    enabled: true
                                    retentionSeconds: 21600
                                    cleanupIntervalSeconds: 3600
                """.trimIndent()
            )
            val settings = settingsService.getCachedSettings(NotificationRecordingSettings::class.java)
            assertTrue(settings.enabled)
            assertEquals(21600, settings.retentionSeconds)
            assertEquals(3600, settings.cleanupIntervalSeconds)
        }
    }

}
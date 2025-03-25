package net.nemerosa.ontrack.extension.hook.settings

import net.nemerosa.ontrack.extension.casc.AbstractCascTestSupport
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parseAsJson
import net.nemerosa.ontrack.model.json.schema.JsonTypeBuilder
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.Duration
import kotlin.test.assertEquals

class HookSettingsCascIT : AbstractCascTestSupport() {

    @Autowired
    private lateinit var hookSettingsCasc: HookSettingsCasc

    @Autowired
    private lateinit var jsonTypeBuilder: JsonTypeBuilder

    @Test
    fun `CasC schema type`() {
        val type = hookSettingsCasc.jsonType(jsonTypeBuilder)
        assertEquals(
            """
                {
                  "title": "HookSettings",
                  "description": null,
                  "properties": {
                    "recordCleanupDuration": {
                      "description": "Maximum time to keep queue records for all kinds of hook requests (counted _after_ the retention)",
                      "type": "string",
                      "pattern": "^\\d+|P(?:\\d+Y)?(?:\\d+M)?(?:\\d+D)?(?:T(?:\\d+H)?(?:\\d+M)?(?:\\d+S)?)?|(\\d+)([smhdwMy])${'$'}"
                    },
                    "recordRetentionDuration": {
                      "description": "Maximum time to keep hook records for non-running requests",
                      "type": "string",
                      "pattern": "^\\d+|P(?:\\d+Y)?(?:\\d+M)?(?:\\d+D)?(?:T(?:\\d+H)?(?:\\d+M)?(?:\\d+S)?)?|(\\d+)([smhdwMy])${'$'}"
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
    fun `Complete set of parameters`() {
        asAdmin {
            withCleanSettings<HookSettings> {
                casc(
                    """
                    ontrack:
                        config:
                            settings:
                                hook:
                                    recordRetentionDuration: 30d
                                    recordCleanupDuration: 120d
                """.trimIndent()
                )
                val settings = cachedSettingsService.getCachedSettings(HookSettings::class.java)
                assertEquals(Duration.ofDays(30), settings.recordRetentionDuration)
                assertEquals(Duration.ofDays(120), settings.recordCleanupDuration)
            }
        }
    }

}
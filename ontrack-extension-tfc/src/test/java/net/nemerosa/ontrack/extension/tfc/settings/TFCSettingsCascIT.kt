package net.nemerosa.ontrack.extension.tfc.settings

import net.nemerosa.ontrack.extension.casc.AbstractCascTestSupport
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parseAsJson
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class TFCSettingsCascIT : AbstractCascTestSupport() {

    @Autowired
    private lateinit var tfcSettingsCasc: TFCSettingsCasc

    @Test
    fun `CasC schema type`() {
        val type = tfcSettingsCasc.jsonType
        assertEquals(
            """
                {
                  "title": "TFCSettings",
                  "description": null,
                  "properties": {
                    "enabled": {
                      "description": "Is the support for TFC notifications enabled?",
                      "type": "boolean"
                    },
                    "token": {
                      "description": "Secret token to be passed by TFC",
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
    fun `Minimal set of parameters`() {
        asAdmin {
            withCleanSettings<TFCSettings> {
                casc(
                    """
                    ontrack:
                        config:
                            settings:
                                tfc:
                                    enabled: true
                """.trimIndent()
                )
                val settings = cachedSettingsService.getCachedSettings(TFCSettings::class.java)
                assertEquals(true, settings.enabled)
            }
        }
    }

}
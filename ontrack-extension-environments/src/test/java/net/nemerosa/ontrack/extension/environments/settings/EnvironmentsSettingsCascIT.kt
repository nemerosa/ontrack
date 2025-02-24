package net.nemerosa.ontrack.extension.environments.settings

import net.nemerosa.ontrack.extension.casc.AbstractCascTestSupport
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parseAsJson
import net.nemerosa.ontrack.model.json.schema.JsonTypeBuilder
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class EnvironmentsSettingsCascIT : AbstractCascTestSupport() {

    @Autowired
    private lateinit var environmentsSettingsCasc: EnvironmentsSettingsCasc

    @Autowired
    private lateinit var jsonTypeBuilder: JsonTypeBuilder

    @Test
    fun `CasC schema type`() {
        val type = environmentsSettingsCasc.jsonType(jsonTypeBuilder)
        assertEquals(
            """
                {
                  "title": "EnvironmentsSettings",
                  "description": null,
                  "properties": {
                    "buildDisplayOption": {
                      "enum": [
                        "ALL",
                        "HIGHEST",
                        "COUNT"
                      ],
                      "description": "How the environments a build is deployed into are displayed",
                      "type": "string",
                      "title": "Enum"
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
    fun `Environment settings`() {
        asAdmin {
            withCleanSettings<EnvironmentsSettings> {
                casc(
                    """
                        ontrack:
                            config:
                                settings:
                                    environments:
                                        buildDisplayOption: ALL
                    """.trimIndent()
                )
                val settings = cachedSettingsService.getCachedSettings(EnvironmentsSettings::class.java)
                assertEquals(
                    EnvironmentsSettings(
                        buildDisplayOption = EnvironmentsSettingsBuildDisplayOption.ALL,
                    ),
                    settings
                )
            }
        }
    }

    @Test
    fun `Rendering the environment settings`() {
        asAdmin {
            withCleanSettings<EnvironmentsSettings> {
                settingsManagerService.saveSettings(
                    EnvironmentsSettings(
                        buildDisplayOption = EnvironmentsSettingsBuildDisplayOption.COUNT,
                    )
                )
                val json = environmentsSettingsCasc.render()
                assertEquals(
                    mapOf(
                        "buildDisplayOption" to "COUNT"
                    ).asJson(),
                    json
                )
            }
        }
    }

}
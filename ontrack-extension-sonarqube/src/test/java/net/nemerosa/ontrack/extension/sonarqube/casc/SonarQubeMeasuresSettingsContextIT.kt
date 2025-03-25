package net.nemerosa.ontrack.extension.sonarqube.casc

import net.nemerosa.ontrack.extension.casc.AbstractCascTestSupport
import net.nemerosa.ontrack.extension.sonarqube.measures.SonarQubeMeasuresSettings
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parseAsJson
import net.nemerosa.ontrack.model.json.schema.JsonTypeBuilder
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class SonarQubeMeasuresSettingsContextIT : AbstractCascTestSupport() {

    @Autowired
    private lateinit var sonarQubeMeasuresSettingsContext: SonarQubeMeasuresSettingsContext

    @Autowired
    private lateinit var jsonTypeBuilder: JsonTypeBuilder

    @Test
    fun `CasC schema type`() {
        val type = sonarQubeMeasuresSettingsContext.jsonType(jsonTypeBuilder)
        assertEquals(
            """
                {
                  "title": "SonarQubeMeasuresSettings",
                  "description": null,
                  "properties": {
                    "blockerThreshold": {
                      "description": "blockerThreshold field",
                      "type": "integer"
                    },
                    "coverageThreshold": {
                      "description": "coverageThreshold field",
                      "type": "integer"
                    },
                    "disabled": {
                      "description": "disabled field",
                      "type": "boolean"
                    },
                    "measures": {
                      "items": {
                        "description": "measures field",
                        "type": "string"
                      },
                      "description": "measures field",
                      "type": "array"
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
    fun `Minimal parameters`() {
        asAdmin {
            withSettings<SonarQubeMeasuresSettings> {
                settingsManagerService.saveSettings(
                    SonarQubeMeasuresSettings(
                        measures = listOf("coverage"),
                        disabled = true,
                        coverageThreshold = 20,
                        blockerThreshold = 1,
                    )
                )
                casc(
                    """
                    ontrack:
                        config:
                            settings:
                                sonarqube-measures:
                                    measures:
                                        - blocker_violations
                                        - coverage
                                        - security_hotspots
                """.trimIndent()
                )
                val settings = cachedSettingsService.getCachedSettings(SonarQubeMeasuresSettings::class.java)
                assertEquals(
                    listOf(
                        "blocker_violations",
                        "coverage",
                        "security_hotspots",
                    ),
                    settings.measures
                )
                assertEquals(false, settings.disabled)
                assertEquals(80, settings.coverageThreshold)
                assertEquals(5, settings.blockerThreshold)
            }
        }
    }

    @Test
    fun `All parameters`() {
        asAdmin {
            withSettings<SonarQubeMeasuresSettings> {
                settingsManagerService.saveSettings(
                    SonarQubeMeasuresSettings(
                        measures = listOf("coverage"),
                        disabled = true,
                        coverageThreshold = 20,
                        blockerThreshold = 1,
                    )
                )
                casc(
                    """
                    ontrack:
                        config:
                            settings:
                                sonarqube-measures:
                                    measures:
                                        - blocker_violations
                                        - coverage
                                        - security_hotspots
                                    disabled: false
                                    coverageThreshold: 80
                                    blockerThreshold: 10
                """.trimIndent()
                )
                val settings = cachedSettingsService.getCachedSettings(SonarQubeMeasuresSettings::class.java)
                assertEquals(
                    listOf(
                        "blocker_violations",
                        "coverage",
                        "security_hotspots",
                    ),
                    settings.measures
                )
                assertEquals(false, settings.disabled)
                assertEquals(80, settings.coverageThreshold)
                assertEquals(10, settings.blockerThreshold)
            }
        }
    }

}
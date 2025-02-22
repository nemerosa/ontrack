package net.nemerosa.ontrack.extension.jenkins.indicator

import net.nemerosa.ontrack.extension.casc.AbstractCascTestSupport
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parseAsJson
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class JenkinsPipelineLibraryIndicatorSettingsCascIT : AbstractCascTestSupport() {

    @Autowired
    private lateinit var casc: JenkinsPipelineLibraryIndicatorSettingsCasc

    @Test
    fun `CasC schema type`() {
        val type = casc.jsonType
        assertEquals(
            """
                {
                  "items": {
                    "title": "JenkinsPipelineLibraryIndicatorLibrarySettings",
                    "description": null,
                    "properties": {
                      "lastDeprecated": {
                        "description": "Last deprecated version",
                        "type": "string"
                      },
                      "lastSupported": {
                        "description": "Last supported version",
                        "type": "string"
                      },
                      "lastUnsupported": {
                        "description": "Last unsupported version",
                        "type": "string"
                      },
                      "library": {
                        "description": "Name of the library",
                        "type": "string"
                      },
                      "required": {
                        "description": "Is the library required?",
                        "type": "boolean"
                      }
                    },
                    "required": [
                      "library"
                    ],
                    "additionalProperties": false,
                    "type": "object"
                  },
                  "description": "List of library versions requirements",
                  "type": "array"
                }
            """.trimIndent().parseAsJson(),
            type.asJson()
        )
    }

    @Test
    fun `Defining library settings as code`() {
        asAdmin {
            casc(
                """
                    ontrack:
                        config:
                            settings:
                                jenkins-pipeline-library-indicator:
                                    - library: standard
                                      required: false
                                      lastSupported: 5
                                      lastDeprecated: 4
                                      lastUnsupported: 3
                                    - library: default
                                      required: true
                                      lastSupported: 6
                                      lastDeprecated: 3
                """
            )
            val settings = cachedSettingsService.getCachedSettings(JenkinsPipelineLibraryIndicatorSettings::class.java)
            assertEquals(
                JenkinsPipelineLibraryIndicatorSettings(
                    libraryVersions = listOf(
                        JenkinsPipelineLibraryIndicatorLibrarySettings(
                            library = "standard",
                            required = false,
                            lastSupported = "5",
                            lastDeprecated = "4",
                            lastUnsupported = "3",
                        ),
                        JenkinsPipelineLibraryIndicatorLibrarySettings(
                            library = "default",
                            required = true,
                            lastSupported = "6",
                            lastDeprecated = "3",
                        ),
                    ),
                ),
                settings
            )
        }
    }

    @Test
    fun `Rendering library settings as code`() {
        asAdmin {
            val settings = JenkinsPipelineLibraryIndicatorSettings(
                libraryVersions = listOf(
                    JenkinsPipelineLibraryIndicatorLibrarySettings(
                        library = "standard",
                        required = false,
                        lastSupported = "5",
                        lastDeprecated = "4",
                        lastUnsupported = "3",
                    ),
                    JenkinsPipelineLibraryIndicatorLibrarySettings(
                        library = "default",
                        required = true,
                        lastSupported = "6",
                        lastDeprecated = "3",
                    ),
                ),
            )
            settingsManagerService.saveSettings(settings)
            val node = casc.render()
            assertEquals(
                listOf(
                    mapOf(
                        "library" to "standard",
                        "required" to false,
                        "lastSupported" to "5",
                        "lastDeprecated" to "4",
                        "lastUnsupported" to "3",
                    ),
                    mapOf(
                        "library" to "default",
                        "required" to true,
                        "lastSupported" to "6",
                        "lastDeprecated" to "3",
                        "lastUnsupported" to null,
                    ),
                ).asJson(),
                node
            )
        }
    }

}
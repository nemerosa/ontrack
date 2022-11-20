package net.nemerosa.ontrack.extension.jenkins.indicator

import net.nemerosa.ontrack.extension.casc.AbstractCascTestSupport
import net.nemerosa.ontrack.json.asJson
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class JenkinsPipelineLibraryIndicatorSettingsCascIT : AbstractCascTestSupport() {

    @Autowired
    private lateinit var casc: JenkinsPipelineLibraryIndicatorSettingsCasc

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
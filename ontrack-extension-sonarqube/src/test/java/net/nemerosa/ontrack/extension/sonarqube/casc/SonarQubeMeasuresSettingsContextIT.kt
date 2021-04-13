package net.nemerosa.ontrack.extension.sonarqube.casc

import net.nemerosa.ontrack.extension.casc.AbstractCascTestSupport
import net.nemerosa.ontrack.extension.sonarqube.measures.SonarQubeMeasuresSettings
import org.junit.Test
import kotlin.test.assertEquals

class SonarQubeMeasuresSettingsContextIT : AbstractCascTestSupport() {

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
                casc("""
                    ontrack:
                        config:
                            settings:
                                sonarqube-measures:
                                    measures:
                                        - blocker_violations
                                        - coverage
                                        - security_hotspots
                """.trimIndent())
                val settings = cachedSettingsService.getCachedSettings(SonarQubeMeasuresSettings::class.java)
                assertEquals(
                    listOf(
                        "blocker_violations",
                        "coverage",
                        "security_hotspots",
                    ),
                    settings.measures
                )
                assertEquals(true, settings.disabled)
                assertEquals(20, settings.coverageThreshold)
                assertEquals(1, settings.blockerThreshold)
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
                casc("""
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
                """.trimIndent())
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
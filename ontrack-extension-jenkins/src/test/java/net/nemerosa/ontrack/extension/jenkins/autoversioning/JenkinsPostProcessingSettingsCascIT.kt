package net.nemerosa.ontrack.extension.jenkins.autoversioning

import net.nemerosa.ontrack.extension.casc.AbstractCascTestSupport
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class JenkinsPostProcessingSettingsCascIT : AbstractCascTestSupport() {

    @Test
    fun `Minimal parameters`() {
        asAdmin {
            withCleanSettings<JenkinsPostProcessingSettings> {
                casc(
                    """
                    ontrack:
                        config:
                            settings:
                                auto-versioning-jenkins:
                                    config: my-config
                                    job: path/to/job
                """.trimIndent()
                )
                val settings = cachedSettingsService.getCachedSettings(JenkinsPostProcessingSettings::class.java)
                assertEquals("my-config", settings.config)
                assertEquals("path/to/job", settings.job)
                assertEquals(10, settings.retries)
                assertEquals(30, settings.retriesDelaySeconds)
            }
        }
    }

    @Test
    fun `Full parameters`() {
        asAdmin {
            withCleanSettings<JenkinsPostProcessingSettings> {
                casc(
                    """
                    ontrack:
                        config:
                            settings:
                                auto-versioning-jenkins:
                                    config: my-config
                                    job: path/to/job
                                    retries: 3
                                    retriesDelaySeconds: 600
                """.trimIndent()
                )
                val settings = cachedSettingsService.getCachedSettings(JenkinsPostProcessingSettings::class.java)
                assertEquals("my-config", settings.config)
                assertEquals("path/to/job", settings.job)
                assertEquals(3, settings.retries)
                assertEquals(600, settings.retriesDelaySeconds)
            }
        }
    }

}
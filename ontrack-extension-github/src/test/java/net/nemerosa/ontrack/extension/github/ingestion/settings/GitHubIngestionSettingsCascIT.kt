package net.nemerosa.ontrack.extension.github.ingestion.settings

import net.nemerosa.ontrack.extension.casc.AbstractCascTestSupport
import org.junit.Test
import kotlin.test.assertEquals

class GitHubIngestionSettingsCascIT : AbstractCascTestSupport() {

    @Test
    fun `Minimal parameters`() {
        asAdmin {
            withSettings<GitHubIngestionSettings> {
                settingsManagerService.saveSettings(
                    GitHubIngestionSettings(
                        token = "old-token",
                        retentionDays = 10,
                        orgProjectPrefix = false,
                        indexationInterval = 10,
                    )
                )
                casc(
                    """
                    ontrack:
                        config:
                            settings:
                                github-ingestion:
                                    token: new-token
                """.trimIndent()
                )
                val settings = cachedSettingsService.getCachedSettings(GitHubIngestionSettings::class.java)
                assertEquals("new-token", settings.token)
                assertEquals(30, settings.retentionDays)
                assertEquals(false, settings.orgProjectPrefix)
                assertEquals(30, settings.indexationInterval)
            }
        }
    }

    @Test
    fun `All parameters`() {
        asAdmin {
            withSettings<GitHubIngestionSettings> {
                settingsManagerService.saveSettings(
                    GitHubIngestionSettings(
                        token = "old-token",
                        retentionDays = 10,
                        orgProjectPrefix = false,
                        indexationInterval = 10,
                    )
                )
                casc(
                    """
                    ontrack:
                        config:
                            settings:
                                github-ingestion:
                                    token: new-token
                                    retentionDays: 60
                                    orgProjectPrefix: true
                                    indexationInterval: 60
                """.trimIndent()
                )
                val settings = cachedSettingsService.getCachedSettings(GitHubIngestionSettings::class.java)
                assertEquals("new-token", settings.token)
                assertEquals(60, settings.retentionDays)
                assertEquals(true, settings.orgProjectPrefix)
                assertEquals(60, settings.indexationInterval)
            }
        }
    }

}
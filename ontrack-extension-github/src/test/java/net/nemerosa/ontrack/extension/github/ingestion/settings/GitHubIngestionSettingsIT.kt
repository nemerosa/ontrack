package net.nemerosa.ontrack.extension.github.ingestion.settings

import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import org.junit.Test
import kotlin.test.assertEquals

class GitHubIngestionSettingsIT : AbstractDSLTestSupport() {

    @Test
    fun `Saving settings`() {
        withSettings<GitHubIngestionSettings> {
            asAdmin {
                settingsManagerService.saveSettings(
                    GitHubIngestionSettings(
                        token = "secret",
                        retentionDays = 10,
                        orgProjectPrefix = false,
                    )
                )
                cachedSettingsService.getCachedSettings(GitHubIngestionSettings::class.java).apply {
                    assertEquals("secret", token)
                    assertEquals(10, retentionDays)
                }
            }
        }
    }

}
package net.nemerosa.ontrack.extension.github.ingestion.settings

import net.nemerosa.ontrack.extension.github.ingestion.AbstractIngestionTestSupport
import org.junit.Test
import kotlin.test.assertEquals

class GitHubIngestionSettingsIT : AbstractIngestionTestSupport() {

    @Test
    fun `Saving settings`() {
        withSettings<GitHubIngestionSettings> {
            asAdmin {
                settingsManagerService.saveSettings(
                    GitHubIngestionSettings(
                        token = "secret",
                        retentionDays = 10,
                        orgProjectPrefix = false,
                        indexationInterval = 60,
                        issueServiceIdentifier = "jira//config",
                        validationJobPrefix = false,
                    )
                )
                cachedSettingsService.getCachedSettings(GitHubIngestionSettings::class.java).apply {
                    assertEquals("secret", token)
                    assertEquals(10, retentionDays)
                    assertEquals(60, indexationInterval)
                    assertEquals("jira//config", issueServiceIdentifier)
                    assertEquals(false, validationJobPrefix)
                }
            }
        }
    }

}
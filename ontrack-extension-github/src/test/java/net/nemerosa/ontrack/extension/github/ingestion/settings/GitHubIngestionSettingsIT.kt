package net.nemerosa.ontrack.extension.github.ingestion.settings

import net.nemerosa.ontrack.extension.github.ingestion.AbstractIngestionTestJUnit4Support
import org.junit.Test
import kotlin.test.assertEquals

class GitHubIngestionSettingsIT : AbstractIngestionTestJUnit4Support() {

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
                        runValidations = true,
                    )
                )
                cachedSettingsService.getCachedSettings(GitHubIngestionSettings::class.java).apply {
                    assertEquals("secret", token)
                    assertEquals(10, retentionDays)
                    assertEquals(60, indexationInterval)
                    assertEquals("jira//config", issueServiceIdentifier)
                    assertEquals(false, validationJobPrefix)
                    assertEquals(true, runValidations)
                }
            }
        }
    }

}
package net.nemerosa.ontrack.extension.github.autoversioning

import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class GitHubPostProcessingSettingsIT : AbstractDSLTestSupport() {

    @Test
    fun `Saving empty settings`() {
        withCleanSettings<GitHubPostProcessingSettings> {
            val settings = GitHubPostProcessingSettings(
                config = null,
                repository = null,
                workflow = null,
                branch = "main",
                retries = 30,
                retriesDelaySeconds = 10,
            )
            asAdmin {
                settingsManagerService.saveSettings(settings)
            }
            val saved = cachedSettingsService.getCachedSettings(GitHubPostProcessingSettings::class.java)
            assertEquals("", saved.config)
            assertEquals("", saved.repository)
            assertEquals("", saved.workflow)
            assertEquals("main", saved.branch)
            assertEquals(30, saved.retries)
            assertEquals(10, saved.retriesDelaySeconds)
        }
    }

}
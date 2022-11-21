package net.nemerosa.ontrack.extension.github.catalog

import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class GitHubSCMCatalogSettingsIT: AbstractDSLTestSupport() {

    @Test
    fun `Edition of the settings`() {
        asAdmin {
            withCleanSettings<GitHubSCMCatalogSettings> {
                val settings = GitHubSCMCatalogSettings(
                    orgs = listOf("nemerosa", "nemerosa-integration"),
                    autoMergeTimeout = 3_600_000L,
                    autoMergeInterval = 180_000L,
                )
                settingsManagerService.saveSettings(settings)
                val saved = cachedSettingsService.getCachedSettings(GitHubSCMCatalogSettings::class.java)
                assertEquals(
                    settings,
                    saved
                )
            }
        }
    }
}
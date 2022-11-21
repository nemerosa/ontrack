package net.nemerosa.ontrack.extension.github.casc

import net.nemerosa.ontrack.extension.casc.AbstractCascTestSupport
import net.nemerosa.ontrack.extension.github.catalog.GitHubSCMCatalogSettings
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class GitHubSCMCatalogSettingsContextIT : AbstractCascTestSupport() {

    @Test
    fun `GitHub SCM Catalog settings as CasC`() {
        asAdmin {
            withSettings<GitHubSCMCatalogSettings> {
                casc("""
                    ontrack:
                        config:
                            settings:
                                github-scm-catalog:
                                    orgs:
                                        - nemerosa
                                        - other
                                    autoMergeTimeout: 3600000
                                    autoMergeInterval: 180000
                """.trimIndent())
                val settings = cachedSettingsService.getCachedSettings(GitHubSCMCatalogSettings::class.java)
                assertEquals(
                    listOf(
                        "nemerosa",
                        "other"
                    ),
                    settings.orgs
                )
                assertEquals(3_600_000L, settings.autoMergeTimeout)
                assertEquals(180_000L, settings.autoMergeInterval)
            }
        }
    }

    @Test
    fun `GitHub SCM Catalog settings as CasC with default interval`() {
        asAdmin {
            withSettings<GitHubSCMCatalogSettings> {
                casc("""
                    ontrack:
                        config:
                            settings:
                                github-scm-catalog:
                                    orgs:
                                        - nemerosa
                                        - other
                """.trimIndent())
                val settings = cachedSettingsService.getCachedSettings(GitHubSCMCatalogSettings::class.java)
                assertEquals(
                    listOf(
                        "nemerosa",
                        "other"
                    ),
                    settings.orgs
                )
                assertEquals(600_000L, settings.autoMergeTimeout)
                assertEquals(30_000L, settings.autoMergeInterval)
            }
        }
    }

}
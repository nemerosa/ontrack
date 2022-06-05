package net.nemerosa.ontrack.extension.github.catalog

import net.nemerosa.ontrack.extension.github.AbstractGitHubTestSupport
import net.nemerosa.ontrack.extension.github.TestOnGitHub
import net.nemerosa.ontrack.extension.github.githubTestConfigReal
import net.nemerosa.ontrack.extension.github.githubTestEnv
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class GitHubSCMCatalogProviderIT : AbstractGitHubTestSupport() {

    @Autowired
    private lateinit var catalogProvider: GitHubSCMCatalogProvider

    @TestOnGitHub
    fun `Getting SCM catalog sources`() {
        val env = githubTestEnv
        val config = githubTestConfigReal()
        val settings = GitHubSCMCatalogSettings(
            orgs = listOf(env.organization),
            autoMergeTimeout = GitHubSCMCatalogSettings.DEFAULT_AUTO_MERGE_TIMEOUT,
            autoMergeInterval = GitHubSCMCatalogSettings.DEFAULT_AUTO_MERGE_INTERVAL,
        )
        val entries = catalogProvider.getConfigEntries(settings, config)
        assertNotNull(entries.find { it.repository == env.fullRepository }) { entry ->
            assertEquals("https://github.com/${env.fullRepository}", entry.repositoryPage)
        }
    }

}
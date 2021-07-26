package net.nemerosa.ontrack.extension.github.catalog

import net.nemerosa.ontrack.extension.github.AbstractGitHubTestSupport
import net.nemerosa.ontrack.extension.github.githubTestConfigReal
import net.nemerosa.ontrack.extension.github.githubTestEnv
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class GitHubSCMCatalogProviderIT : AbstractGitHubTestSupport() {

    @Autowired
    private lateinit var catalogProvider: GitHubSCMCatalogProvider

    @Test
    fun `Getting SCM catalog sources`() {
        val env = githubTestEnv
        val config = githubTestConfigReal()
        val settings = GitHubSCMCatalogSettings(
            orgs = listOf(env.organization)
        )
        val entries = catalogProvider.getConfigEntries(settings, config)
        assertNotNull(entries.find { it.repository == env.fullRepository }) { entry ->
            assertEquals("https://github.com/${env.fullRepository}", entry.repositoryPage)
        }
    }

}
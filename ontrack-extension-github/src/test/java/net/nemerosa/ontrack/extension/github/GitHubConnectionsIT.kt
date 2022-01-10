package net.nemerosa.ontrack.extension.github

import net.nemerosa.ontrack.extension.git.model.gitRepository
import net.nemerosa.ontrack.extension.git.service.GitService
import net.nemerosa.ontrack.extension.github.client.OntrackGitHubClientFactory
import net.nemerosa.ontrack.extension.github.model.GitHubEngineConfiguration
import net.nemerosa.ontrack.extension.github.property.GitHubProjectConfigurationProperty
import net.nemerosa.ontrack.extension.github.property.GitHubProjectConfigurationPropertyType
import net.nemerosa.ontrack.git.GitRepositoryClientFactory
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.test.fail

/**
 * Testings all connection modes
 */
class GitHubConnectionsIT : AbstractGitHubTestJUnit4Support() {

    @Autowired
    private lateinit var clientFactory: OntrackGitHubClientFactory

    @Autowired
    private lateinit var gitRepositoryClientFactory: GitRepositoryClientFactory

    @Autowired
    private lateinit var gitService: GitService

    @Test
    fun `Username and token access`() {
        runTest(
            GitHubEngineConfiguration(
                name = uid("GH"),
                url = null,
                user = githubTestEnv.user,
                oauth2Token = githubTestEnv.token,
            )
        )
    }

    @Test
    fun `Token only access`() {
        runTest(
            GitHubEngineConfiguration(
                name = uid("GH"),
                url = null,
                oauth2Token = githubTestEnv.token,
            )
        )
    }

    @Test
    fun `App access`() {
        runTest(
            GitHubEngineConfiguration(
                name = uid("GH"),
                url = null,
                appId = githubTestEnv.appId,
                appPrivateKey = githubTestEnv.appPrivateKey,
                appInstallationAccountName = githubTestEnv.appInstallationAccountName,
            )
        )
    }

    private fun runTest(
        configuration: GitHubEngineConfiguration,
    ) {
        // Creates & saves the GitHub configuration
        asAdmin {
            gitConfigurationService.newConfiguration(configuration)
        }
        // Testing the API
        val client = clientFactory.create(configuration)
        val issue = client.getIssue(githubTestEnv.fullRepository, githubTestEnv.issue)
        assertNotNull(issue, "Issue ${githubTestEnv.fullRepository}#${githubTestEnv.issue} has been found")
        // Testing the local sync
        project {
            setProperty(
                this, GitHubProjectConfigurationPropertyType::class.java, GitHubProjectConfigurationProperty(
                    configuration = configuration,
                    repository = githubTestEnv.fullRepository,
                    indexationInterval = 0,
                    issueServiceConfigurationIdentifier = null,
                )
            )
            val gitConfiguration = gitService.getProjectConfiguration(this) ?: fail("No Git project configuration")
            val gitRepositoryClient = gitRepositoryClientFactory.getClient(gitConfiguration.gitRepository)
            gitRepositoryClient.sync { println(it) }
            gitRepositoryClient.test()
            assertTrue(gitRepositoryClient.isReady, "Repository is ready to be used")
            // Getting the readme to make sure
            val readme = gitRepositoryClient.download(githubTestEnv.branch, githubTestEnv.readme)
            assertNotNull(readme) {
                assertTrue(it.isNotBlank(), "The readme could be downloaded")
            }
        }
    }

}
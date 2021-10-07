package net.nemerosa.ontrack.extension.github

import net.nemerosa.ontrack.extension.github.client.OntrackGitHubClientFactory
import net.nemerosa.ontrack.extension.github.model.GitHubEngineConfiguration
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertNotNull

/**
 * Testings all connection modes
 */
class GitHubConnectionsIT : AbstractGitHubTestSupport() {

    @Autowired
    private lateinit var clientFactory: OntrackGitHubClientFactory

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
        // TODO Testing the local sync
    }

}
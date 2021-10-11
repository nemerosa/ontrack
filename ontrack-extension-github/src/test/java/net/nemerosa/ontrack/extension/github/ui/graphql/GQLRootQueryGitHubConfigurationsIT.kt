package net.nemerosa.ontrack.extension.github.ui.graphql

import net.nemerosa.ontrack.extension.github.AbstractGitHubTestSupport
import net.nemerosa.ontrack.extension.github.githubTestEnv
import net.nemerosa.ontrack.extension.github.model.GitHubEngineConfiguration
import net.nemerosa.ontrack.json.getTextField
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class GQLRootQueryGitHubConfigurationsIT : AbstractGitHubTestSupport() {

    @Test
    fun `Getting the list of configurations`() {
        val config = gitHubConfig()
        run(
            """
            {
                gitHubConfigurations {
                    name
                    url
                    user
                    appId
                    appInstallationAccountName
                }
            }
        """
        ) { data ->
            val node = data["gitHubConfigurations"].find { it.getTextField("name") == config.name }
            assertNotNull(node) {
                assertEquals("https://github.com", it.getTextField("url"))
            }
        }
    }

    @Test
    fun `Getting information about a real configuration`() {
        val config = GitHubEngineConfiguration(
            name = uid("GH"),
            url = null,
            appId = githubTestEnv.appId,
            appPrivateKey = githubTestEnv.appPrivateKey,
            appInstallationAccountName = githubTestEnv.appInstallationAccountName,
        )
        asAdmin {
            gitConfigurationService.newConfiguration(config)
        }
        run(
            """
            {
                gitHubConfigurations(name: "${config.name}") {
                    name
                    url
                    user
                    appId
                    appInstallationAccountName
                    authenticationType
                    appToken {
                        valid
                        validUntil
                    }
                    rateLimits {
                      core {
                        limit
                        used
                      }
                      graphql {
                        limit
                        used
                      }
                    }
                }
            }
        """
        ) { data ->
            val node = data["gitHubConfigurations"].first()
            assertNotNull(node) {
                assertEquals(config.name, it.getTextField("name"))
                assertEquals("https://github.com", it.getTextField("url"))
                assertEquals("APP", it.getTextField("authenticationType"))
                assertTrue(it.path("rateLimits").path("core").path("limit").asInt() > 0)
                assertTrue(it.path("rateLimits").path("graphql").path("limit").asInt() > 0)
                assertTrue(it.path("appToken").path("valid").asBoolean(), "App token is still valid")
            }
        }
    }

}
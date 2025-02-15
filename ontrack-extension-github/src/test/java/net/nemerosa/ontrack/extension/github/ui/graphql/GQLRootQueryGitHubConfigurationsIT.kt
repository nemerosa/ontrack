package net.nemerosa.ontrack.extension.github.ui.graphql

import net.nemerosa.ontrack.extension.github.AbstractGitHubTestSupport
import net.nemerosa.ontrack.extension.github.TestOnGitHub
import net.nemerosa.ontrack.extension.github.githubTestEnv
import net.nemerosa.ontrack.extension.github.model.GitHubEngineConfiguration
import net.nemerosa.ontrack.json.getTextField
import net.nemerosa.ontrack.test.TestUtils.uid
import net.nemerosa.ontrack.test.assertJsonNull
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class GQLRootQueryGitHubConfigurationsIT : AbstractGitHubTestSupport() {

    @Test
    fun `Getting the list of configurations`() {
        val config = gitHubConfiguration()
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

    @TestOnGitHub
    fun `Getting information about a real configuration using app authentication`() {
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
                        createdAt
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

    @TestOnGitHub
    fun `Getting information about a real configuration using token authentication`() {
        val config = GitHubEngineConfiguration(
            name = uid("GH"),
            url = null,
            oauth2Token = githubTestEnv.token,
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
                        createdAt
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
                assertEquals("TOKEN", it.getTextField("authenticationType"))
                assertTrue(it.path("rateLimits").path("core").path("limit").asInt() > 0)
                assertTrue(it.path("rateLimits").path("graphql").path("limit").asInt() > 0)
                assertJsonNull(it.path("appToken"))
            }
        }
    }

}
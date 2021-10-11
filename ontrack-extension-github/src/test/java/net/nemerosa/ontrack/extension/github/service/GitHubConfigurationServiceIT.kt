package net.nemerosa.ontrack.extension.github.service

import net.nemerosa.ontrack.extension.github.AbstractGitHubTestSupport
import net.nemerosa.ontrack.extension.github.model.GitHubEngineConfiguration
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.Test
import kotlin.test.assertEquals

class GitHubConfigurationServiceIT : AbstractGitHubTestSupport() {

    @Test
    fun `Password is kept on save`() {
        val config = GitHubEngineConfiguration(
            name = uid("GH"),
            url = null,
            user = "test",
            password = "xxxxx",
        )
        withDisabledConfigurationTest {
            asAdmin {
                gitConfigurationService.newConfiguration(config)
                // Saves the config without specifying the password
                gitConfigurationService.updateConfiguration(
                    config.name,
                    GitHubEngineConfiguration(
                        name = config.name,
                        url = null,
                        user = "test",
                        password = "", // Blank
                    )
                )
                // Checks the password is still there
                assertEquals("xxxxx", gitConfigurationService.getConfiguration(config.name).password)
            }
        }
    }

    @Test
    fun `Token is kept on save`() {
        val config = GitHubEngineConfiguration(
            name = uid("GH"),
            url = null,
            oauth2Token = "xxxxx",
        )
        withDisabledConfigurationTest {
            asAdmin {
                gitConfigurationService.newConfiguration(config)
                // Saves the config without specifying the token
                gitConfigurationService.updateConfiguration(
                    config.name,
                    GitHubEngineConfiguration(
                        name = config.name,
                        url = null,
                        oauth2Token = "", // Blank
                    )
                )
                // Checks the password is still there
                assertEquals("xxxxx", gitConfigurationService.getConfiguration(config.name).oauth2Token)
            }
        }
    }

    @Test
    fun `App private key is kept on save`() {
        val config = GitHubEngineConfiguration(
            name = uid("GH"),
            url = null,
            appId = "123456",
            appPrivateKey = "xxxxx",
        )
        withDisabledConfigurationTest {
            asAdmin {
                gitConfigurationService.newConfiguration(config)
                // Saves the config without specifying the key
                gitConfigurationService.updateConfiguration(
                    config.name,
                    GitHubEngineConfiguration(
                        name = config.name,
                        url = null,
                        appId = "123456",
                        appPrivateKey = "", // Blank
                    )
                )
                // Checks the password is still there
                assertEquals("xxxxx", gitConfigurationService.getConfiguration(config.name).appPrivateKey)
            }
        }
    }

}
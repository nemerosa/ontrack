package net.nemerosa.ontrack.extension.github.service

import net.nemerosa.ontrack.extension.github.AbstractGitHubTestSupport
import net.nemerosa.ontrack.extension.github.model.GitHubAuthenticationType
import net.nemerosa.ontrack.extension.github.model.GitHubEngineConfiguration
import net.nemerosa.ontrack.model.support.ConfigurationRepository
import net.nemerosa.ontrack.test.TestUtils
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class GitHubConfigurationServiceIT : AbstractGitHubTestSupport() {

    @Autowired
    private lateinit var configurationRepository: ConfigurationRepository

    @Test
    fun `Updating a GitHub configuration to use a GitHub app`() {
        withDisabledConfigurationTest {
            asAdmin {
                // Creating the initial configuration with a token
                val config = gitConfigurationService.newConfiguration(
                    GitHubEngineConfiguration(
                        name = uid("GH"),
                        url = null,
                        oauth2Token = "xxxxx",
                    )
                )
                // Updating this configuration to use an app
                gitConfigurationService.updateConfiguration(
                    config.name,
                    GitHubEngineConfiguration(
                        name = config.name,
                        url = config.url,
                        oauth2Token = "", // Not filled in
                        appId = "123456",
                        appPrivateKey = TestUtils.resourceString("/test-app.pem"),
                    )
                )
                // Checks the new configuration
                assertEquals(
                    GitHubAuthenticationType.APP,
                    gitConfigurationService.getConfiguration(config.name).authenticationType()
                )
            }
        }
    }

    @Test
    fun `Encrypting and decrypting the token`() {
        val config = GitHubEngineConfiguration(
            name = uid("GH"),
            url = null,
            oauth2Token = "xxxxx",
        )
        withDisabledConfigurationTest {
            asAdmin {
                gitConfigurationService.newConfiguration(config)
                // Gets the raw configuration
                assertNotNull(
                    configurationRepository.find(GitHubEngineConfiguration::class.java, config.name)
                ) {
                    // Checks it's encrypted
                    assertFalse(it.oauth2Token.isNullOrBlank(), "Token is saved")
                    assertTrue(it.oauth2Token != "xxxxx", "Token is not plain")
                }
                // Loading the configuration
                gitConfigurationService.getConfiguration(config.name).apply {
                    // Checks its token has been decrypted
                    assertEquals("xxxxx", oauth2Token)
                }
            }
        }
    }

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
        val appPrivateKey = TestUtils.resourceString("/test-app.pem")
        val config = GitHubEngineConfiguration(
            name = uid("GH"),
            url = null,
            appId = "123456",
            appPrivateKey = appPrivateKey, // We do need a valid key for the test
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
                assertEquals(appPrivateKey, gitConfigurationService.getConfiguration(config.name).appPrivateKey)
            }
        }
    }

}
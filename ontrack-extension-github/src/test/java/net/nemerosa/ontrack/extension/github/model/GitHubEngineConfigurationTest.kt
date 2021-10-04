package net.nemerosa.ontrack.extension.github.model

import io.mockk.every
import io.mockk.mockk
import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.common.toJavaDate
import net.nemerosa.ontrack.extension.github.app.MockGitHubAppClient
import net.nemerosa.ontrack.extension.github.app.client.GitHubAppAccount
import net.nemerosa.ontrack.extension.github.app.client.GitHubAppClient
import net.nemerosa.ontrack.extension.github.app.client.GitHubAppInstallation
import net.nemerosa.ontrack.extension.github.app.client.GitHubAppInstallationToken
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.test.TestUtils
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.Test
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class GitHubEngineConfigurationTest {

    @Test
    fun obfuscation_of_password() {
        val configuration = GitHubEngineConfiguration(
            "ontrack",
            GitHubEngineConfiguration.GITHUB_COM,
            "user",
            "secret",
            null
        )
        assertEquals(null, configuration.obfuscate().password)
    }

    @Test
    fun obfuscation_of_token() {
        val configuration = GitHubEngineConfiguration(
            "ontrack",
            GitHubEngineConfiguration.GITHUB_COM,
            null,
            null,
            "1234567890abcdef"
        )
        assertEquals(null, configuration.obfuscate().oauth2Token)
    }

    @Test
    fun `Obfuscation of app private key`() {
        val configuration = GitHubEngineConfiguration(
            "ontrack",
            GitHubEngineConfiguration.GITHUB_COM,
            appId = "123456",
            appPrivateKey = "xxxxxxx",
        )
        assertEquals(null, configuration.obfuscate().appPrivateKey)
    }

    @Test
    fun `Anonymous to json`() {
        assertEquals(
            mapOf(
                "name" to "ontrack",
                "url" to "https://github.com",
                "user" to null,
                "password" to null,
                "oauth2Token" to null,
                "appId" to null,
                "appPrivateKey" to null,
                "appInstallationAccountName" to null,
            ).asJson(),
            GitHubEngineConfiguration(
                "ontrack",
                GitHubEngineConfiguration.GITHUB_COM,
            ).asJson()
        )
    }

    @Test
    fun `Anonymous from JSON`() {
        assertEquals(
            GitHubEngineConfiguration(
                "ontrack",
                GitHubEngineConfiguration.GITHUB_COM,
            ),
            mapOf(
                "name" to "ontrack",
                "url" to "https://github.com",
                "user" to null,
                "password" to null,
                "oauth2Token" to null,
                "appId" to null,
                "appPrivateKey" to null,
                "appInstallationAccountName" to null,
            ).asJson().parse()
        )
    }

    @Test
    fun `Token to json`() {
        assertEquals(
            mapOf(
                "name" to "ontrack",
                "url" to "https://github.com",
                "user" to null,
                "password" to null,
                "oauth2Token" to "1234567890abcdef",
                "appId" to null,
                "appPrivateKey" to null,
                "appInstallationAccountName" to null,
            ).asJson(),
            GitHubEngineConfiguration(
                "ontrack",
                GitHubEngineConfiguration.GITHUB_COM,
                null,
                null,
                "1234567890abcdef"
            ).asJson()
        )
    }

    @Test
    fun `Token from JSON`() {
        assertEquals(
            GitHubEngineConfiguration(
                "ontrack",
                GitHubEngineConfiguration.GITHUB_COM,
                null,
                null,
                "1234567890abcdef"
            ),
            mapOf(
                "name" to "ontrack",
                "url" to "https://github.com",
                "user" to null,
                "password" to null,
                "oauth2Token" to "1234567890abcdef",
                "appId" to null,
                "appPrivateKey" to null,
                "appInstallationAccountName" to null,
            ).asJson().parse()
        )
    }

    @Test
    fun `App to json`() {
        assertEquals(
            mapOf(
                "name" to "ontrack",
                "url" to "https://github.com",
                "user" to null,
                "password" to null,
                "oauth2Token" to null,
                "appId" to "123456",
                "appPrivateKey" to "xxxxxxx",
                "appInstallationAccountName" to null,
            ).asJson(),
            GitHubEngineConfiguration(
                "ontrack",
                GitHubEngineConfiguration.GITHUB_COM,
                appId = "123456",
                appPrivateKey = "xxxxxxx",
            ).asJson()
        )
    }

    @Test
    fun `App from JSON`() {
        assertEquals(
            GitHubEngineConfiguration(
                "ontrack",
                GitHubEngineConfiguration.GITHUB_COM,
                appId = "123456",
                appPrivateKey = "xxxxxxx",
            ),
            mapOf(
                "name" to "ontrack",
                "url" to "https://github.com",
                "user" to null,
                "password" to null,
                "oauth2Token" to null,
                "appId" to "123456",
                "appPrivateKey" to "xxxxxxx",
                "appInstallationAccountName" to null,
            ).asJson().parse()
        )
    }

    @Test
    fun `App installation to json`() {
        assertEquals(
            mapOf(
                "name" to "ontrack",
                "url" to "https://github.com",
                "user" to null,
                "password" to null,
                "oauth2Token" to null,
                "appId" to "123456",
                "appPrivateKey" to "xxxxxxx",
                "appInstallationAccountName" to "test",
            ).asJson(),
            GitHubEngineConfiguration(
                "ontrack",
                GitHubEngineConfiguration.GITHUB_COM,
                appId = "123456",
                appPrivateKey = "xxxxxxx",
                appInstallationAccountName = "test",
            ).asJson()
        )
    }

    @Test
    fun `App installation from JSON`() {
        assertEquals(
            GitHubEngineConfiguration(
                "ontrack",
                GitHubEngineConfiguration.GITHUB_COM,
                appId = "123456",
                appPrivateKey = "xxxxxxx",
                appInstallationAccountName = "test",
            ),
            mapOf(
                "name" to "ontrack",
                "url" to "https://github.com",
                "user" to null,
                "password" to null,
                "oauth2Token" to null,
                "appId" to "123456",
                "appPrivateKey" to "xxxxxxx",
                "appInstallationAccountName" to "test",
            ).asJson().parse()
        )
    }

    @Test
    fun null_url_for_github_com() {
        val configuration = GitHubEngineConfiguration(
            "Test",
            null,
            "",
            "",
            ""
        )
        assertEquals("https://github.com", configuration.url)
    }

    @Test
    fun empty_url_for_github_com() {
        val configuration = GitHubEngineConfiguration(
            "Test",
            "",
            "",
            "",
            ""
        )
        assertEquals("https://github.com", configuration.url)
    }

    @Test
    fun `Anonymous form does not contain any credentials`() {
        GitHubEngineConfiguration("Test", null).asForm().apply {
            assertEquals(null, getField("user")?.value)
            assertEquals(null, getField("password")?.value)
            assertEquals(null, getField("oauth2Token")?.value)
            assertEquals(null, getField("appId")?.value)
            assertEquals(null, getField("appPrivateKey")?.value)
        }
    }

    @Test
    fun `Password form does not contain any credentials`() {
        GitHubEngineConfiguration(
            "Test", null,
            user = "user",
            password = "xxxx"
        ).asForm().apply {
            assertEquals("user", getField("user")?.value)
            assertEquals(null, getField("password")?.value)
            assertEquals(null, getField("oauth2Token")?.value)
            assertEquals(null, getField("appId")?.value)
            assertEquals(null, getField("appPrivateKey")?.value)
        }
    }

    @Test
    fun `Token form does not contain any credentials`() {
        GitHubEngineConfiguration(
            "Test", null,
            oauth2Token = "token",
        ).asForm().apply {
            assertEquals(null, getField("user")?.value)
            assertEquals(null, getField("password")?.value)
            assertEquals(null, getField("oauth2Token")?.value)
            assertEquals(null, getField("appId")?.value)
            assertEquals(null, getField("appPrivateKey")?.value)
        }
    }

    @Test
    fun `App form does not contain any credentials`() {
        GitHubEngineConfiguration(
            "Test", null,
            appId = "123456",
            appPrivateKey = "xxxxxxx"
        ).asForm().apply {
            assertEquals(null, getField("user")?.value)
            assertEquals(null, getField("password")?.value)
            assertEquals(null, getField("oauth2Token")?.value)
            assertEquals("123456", getField("appId")?.value)
            assertEquals(null, getField("appPrivateKey")?.value)
        }
    }

    @Test
    fun `Anonymous mode`() {
        assertEquals(
            GitHubAuthenticationType.ANONYMOUS,
            GitHubEngineConfiguration("Test", null).authenticationType()
        )
    }

    @Test
    fun `Password mode`() {
        assertEquals(
            GitHubAuthenticationType.PASSWORD,
            GitHubEngineConfiguration(
                "Test", null,
                user = "user",
                password = "xxx",
            ).authenticationType()
        )
    }

    @Test
    fun `User token mode`() {
        assertEquals(
            GitHubAuthenticationType.USER_TOKEN,
            GitHubEngineConfiguration(
                "Test", null,
                user = "user",
                oauth2Token = "xxx",
            ).authenticationType()
        )
    }

    @Test
    fun `Token mode`() {
        assertEquals(
            GitHubAuthenticationType.TOKEN,
            GitHubEngineConfiguration(
                "Test", null,
                oauth2Token = "xxx",
            ).authenticationType()
        )
    }

    @Test
    fun `App mode`() {
        assertEquals(
            GitHubAuthenticationType.APP,
            GitHubEngineConfiguration(
                "Test", null,
                appId = "123456",
                appPrivateKey = "xxx",
            ).authenticationType()
        )
    }

    @Test
    fun `App mode takes precedence`() {
        assertEquals(
            GitHubAuthenticationType.APP,
            GitHubEngineConfiguration(
                "Test", null,
                user = "user",
                password = "xxx",
                oauth2Token = "xxx",
                appId = "123456",
                appPrivateKey = "xxx",
            ).authenticationType()
        )
    }

    @Test
    fun `GitHub App tokens are only available when the GitHub app is configured`() {
        val config = GitHubEngineConfiguration("Test", null, oauth2Token = "xxxxx")
        assertFailsWith<IllegalStateException> {
            config.getAppInstallationToken(MockGitHubAppClient())
        }
    }

    @Test
    fun `Getting a new token when there is none yet`() {
        val config = gitHubAppConfig()
        val client = mockk<GitHubAppClient>()

        every { client.getAppInstallations(any()) } returns listOf(
            GitHubAppInstallation(
                TEST_APP_INSTALLATION_ID,
                GitHubAppAccount(TEST_APP_INSTALLATION_ACCOUNT_LOGIN)
            )
        )
        every {
            client.generateInstallationToken(
                any(),
                TEST_APP_INSTALLATION_ID
            )
        } returns uniqueTestInstallationToken()

        val token = config.getAppInstallationToken(client)
        assertTrue(token.isNotBlank(), "Token has been generated")

        // Gets the token again, it must be the same
        val secondToken = config.getAppInstallationToken(client)
        assertEquals(secondToken, token, "Reusing the same token")
    }

    @Test
    fun `Getting a new token after it's been invalidated`() {
        val config = gitHubAppConfig()
        val client = mockk<GitHubAppClient>()

        every { client.getAppInstallations(any()) } returns listOf(
            GitHubAppInstallation(
                TEST_APP_INSTALLATION_ID,
                GitHubAppAccount(TEST_APP_INSTALLATION_ACCOUNT_LOGIN)
            )
        )
        every { client.generateInstallationToken(any(), TEST_APP_INSTALLATION_ID) } returns
                uniqueTestInstallationToken() andThen
                uniqueTestInstallationToken()

        // Gets the first token
        val token = config.getAppInstallationToken(client)
        assertTrue(token.isNotBlank(), "Token has been generated")

        // Invalidates the token
        config.invalidateAppInstallationToken()

        // Regenerates the token
        val secondToken = config.getAppInstallationToken(client)
        assertTrue(secondToken != token, "Token has been changed")
    }

    private fun uniqueTestInstallationToken() = GitHubAppInstallationToken(
        uid("T"),
        Time.now().plusHours(1).toJavaDate()
    )

    private fun gitHubAppConfig() = GitHubEngineConfiguration(
        name = uid("C"),
        url = null,
        appId = TEST_APP_ID,
        appPrivateKey = TestUtils.resourceString(TEST_APP_PRIVATE_KEY_RESOURCE_PATH),
        appInstallationAccountName = null,
    )

    companion object {
        const val TEST_APP_ID = "123456"
        const val TEST_APP_PRIVATE_KEY_RESOURCE_PATH = "/test-app.pem"
        const val TEST_APP_INSTALLATION_ID = "1234567890"
        const val TEST_APP_INSTALLATION_ACCOUNT_LOGIN = "test"
    }
}
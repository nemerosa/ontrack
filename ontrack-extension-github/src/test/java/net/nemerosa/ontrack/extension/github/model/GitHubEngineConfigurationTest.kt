package net.nemerosa.ontrack.extension.github.model

import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parse
import org.junit.Test
import kotlin.test.assertEquals

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
}
package net.nemerosa.ontrack.extension.github.app

import net.nemerosa.ontrack.test.TestUtils
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class GitHubAppTest {

    private val testAppId = "123456"
    private val testAppPrivateKeyResourcePath = "/test-app.pem"

    @Test
    fun `Reading the private key`() {
        val pem = TestUtils.resourceString(testAppPrivateKeyResourcePath)
        val key = GitHubApp.readPrivateKey(pem)
        assertEquals("RSA", key.algorithm)
    }

    @Test
    fun `Generating the key`() {
        val token = testJwt()
        // Simple check
        assertTrue(token.isNotBlank(), "JWT has been generated")
    }

    @Test
    fun `Getting the default installation`() {
        // JWT
        val jwt = testJwt()
        // Mocking
        val client = MockGitHubAppClient().registerInstallation(jwt, "123456789", "test")
        // Getting the installation
        val installationId = GitHubApp(client).getInstallation(jwt, testAppId, null)
        assertEquals("123456789", installationId)
    }

    @Test
    fun `Getting no installation`() {
        // JWT
        val jwt = testJwt()
        // Mocking
        val client = MockGitHubAppClient()
        // Getting the installation
        assertFailsWith<GitHubAppNoInstallationException> {
            GitHubApp(client).getInstallation(jwt, testAppId, null)
        }
    }

    @Test
    fun `Installation does not match the required account`() {
        // JWT
        val jwt = testJwt()
        // Mocking
        val client = MockGitHubAppClient().registerInstallation(jwt, "123456789", "test")
        // Getting the installation
        assertFailsWith<GitHubAppNoInstallationForAccountException> {
            GitHubApp(client).getInstallation(jwt, testAppId, "987654321")
        }
    }

    @Test
    fun `Too many installations without an account name`() {
        // JWT
        val jwt = testJwt()
        // Mocking
        val client = MockGitHubAppClient()
            .registerInstallation(jwt, "123456789", "test-1")
            .registerInstallation(jwt, "234567890", "test-2")
        // Getting the installation
        assertFailsWith<GitHubAppSeveralInstallationsException> {
            GitHubApp(client).getInstallation(jwt, testAppId, null)
        }
    }

    @Test
    fun `Match on the installation account`() {
        // JWT
        val jwt = testJwt()
        // Mocking
        val client = MockGitHubAppClient()
            .registerInstallation(jwt, "123456789", "test-1")
            .registerInstallation(jwt, "234567890", "test-2")
        // Getting the installation
        val installationId = GitHubApp(client).getInstallation(jwt, testAppId, "test-2")
        assertEquals("234567890", installationId)
    }

    @Test
    fun `Installation token`() {
        // JWT
        val jwt = testJwt()
        // Mocking
        val client = MockGitHubAppClient()
        // Installation token
        val token = GitHubApp(client).generateInstallationToken(jwt, "1234567890")
        assertEquals("12345678900000", token.token)
    }

    private fun testJwt(): String {
        // Reading the private key
        val testAppPrivateKey = TestUtils.resourceString(testAppPrivateKeyResourcePath)
        // Generation
        return GitHubApp.generateJWT(testAppId, testAppPrivateKey)
    }

}
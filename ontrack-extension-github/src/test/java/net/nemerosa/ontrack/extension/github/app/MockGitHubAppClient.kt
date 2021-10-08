package net.nemerosa.ontrack.extension.github.app

import net.nemerosa.ontrack.extension.github.app.client.GitHubAppAccount
import net.nemerosa.ontrack.extension.github.app.client.GitHubAppClient
import net.nemerosa.ontrack.extension.github.app.client.GitHubAppInstallation
import net.nemerosa.ontrack.extension.github.app.client.GitHubAppInstallationToken
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

/**
 * Mocking app client
 */
class MockGitHubAppClient : GitHubAppClient {

    private val installations: MutableMap<String, MutableList<GitHubAppInstallation>> = mutableMapOf()

    fun clearInstallations() {
        installations.clear()
    }

    private fun registerInstallation(jwt: String, gitHubAppInstallation: GitHubAppInstallation): MockGitHubAppClient {
        installations.getOrPut(jwt) { mutableListOf() }.add(gitHubAppInstallation)
        return this
    }

    fun registerInstallation(
        jwt: String,
        installationId: String,
        installationAccountName: String
    ): MockGitHubAppClient =
        registerInstallation(
            jwt,
            GitHubAppInstallation(
                id = installationId,
                account = GitHubAppAccount(
                    login = installationAccountName,
                    url = "uri://installation",
                )
            )
        )

    override fun getAppInstallations(jwt: String): List<GitHubAppInstallation> =
        installations[jwt] ?: emptyList()

    override fun generateInstallationToken(jwt: String, appInstallationId: String) = GitHubAppInstallationToken(
        token = "${appInstallationId}0000",
        expiresAt = Date.from(LocalDateTime.now().plusHours(1).atZone(ZoneId.systemDefault()).toInstant())
    )

}
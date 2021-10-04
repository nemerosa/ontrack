package net.nemerosa.ontrack.extension.github.app

import net.nemerosa.ontrack.extension.github.app.client.GitHubAppAccount
import net.nemerosa.ontrack.extension.github.app.client.GitHubAppClient
import net.nemerosa.ontrack.extension.github.app.client.GitHubAppInstallation

/**
 * Mocking app client
 */
class MockGitHubAppClient : GitHubAppClient {

    private val installations: MutableMap<String, MutableList<GitHubAppInstallation>> = mutableMapOf()

    fun clearInstallations() {
        installations.clear()
    }

    fun registerInstallation(jwt: String, gitHubAppInstallation: GitHubAppInstallation): MockGitHubAppClient {
        installations.getOrPut(jwt) { mutableListOf() }.add(gitHubAppInstallation)
        return this
    }

    fun registerInstallation(jwt: String, installationId: String, installationAccountName: String): MockGitHubAppClient =
        registerInstallation(
            jwt,
            GitHubAppInstallation(
                id = installationId,
                account = GitHubAppAccount(
                    login = installationAccountName
                )
            )
        )

    override fun getAppInstallations(jwt: String): List<GitHubAppInstallation> =
        installations[jwt] ?: emptyList()

}
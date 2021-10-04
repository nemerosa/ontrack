package net.nemerosa.ontrack.extension.github.app.client

import net.nemerosa.ontrack.extension.github.app.GitHubApp.Companion.generateJWT

/**
 * Abstraction for a GitHub App client.
 */
interface GitHubAppClient {

    /**
     * List of GitHub App installations for a GitHub App.
     *
     * See
     *
     * @param jwt JWT to use for authentication.
     * @return List of installations for this application
     */
    fun getAppInstallations(jwt: String): List<GitHubAppInstallation>

    /**
     * Gets a new token for an app installation
     *
     * @param jwt JWT to use for authentication.
     * @param appInstallationId ID of the GitHub App installation
     * @return Token & its expiration date
     */
    fun generateInstallationToken(jwt: String, appInstallationId: String): GitHubAppInstallationToken

}
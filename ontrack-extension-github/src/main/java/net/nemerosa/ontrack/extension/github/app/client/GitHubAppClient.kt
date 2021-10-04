package net.nemerosa.ontrack.extension.github.app.client

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

}
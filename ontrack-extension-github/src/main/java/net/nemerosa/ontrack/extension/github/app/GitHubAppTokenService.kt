package net.nemerosa.ontrack.extension.github.app

import net.nemerosa.ontrack.extension.github.app.client.GitHubAppAccount

/**
 * Service used to link GitHub configurations to their GitHub App token, including their lifecycle.
 */
interface GitHubAppTokenService {

    /**
     * Gets the current app installation access token, renews it if needed, and returns `null` if
     * the token cannot get generated in any way.
     */
    fun getAppInstallationToken(
        configurationName: String,
        appId: String,
        appPrivateKey: String,
        appInstallationAccountName: String?,
    ): String?

    /**
     * Gets the current app installation access token information, renews it if needed, and returns `null` if
     * the token cannot get generated in any way.
     */
    fun getAppInstallationTokenInformation(
        configurationName: String,
        appId: String,
        appPrivateKey: String,
        appInstallationAccountName: String?,
    ): GitHubAppToken?

    /**
     * Invalidates any current token.
     */
    fun invalidateAppInstallationToken(configurationName: String)

    /**
     * Gets the account of the installation used by this app.
     */
    fun getAppInstallationAccount(
        configurationName: String,
        appId: String,
        appPrivateKey: String,
        appInstallationAccountName: String?,
    ): GitHubAppAccount?

}
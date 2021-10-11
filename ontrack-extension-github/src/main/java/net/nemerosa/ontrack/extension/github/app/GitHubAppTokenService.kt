package net.nemerosa.ontrack.extension.github.app

import net.nemerosa.ontrack.extension.github.app.client.GitHubAppAccount

/**
 * Service used to link GitHub configurations to their GitHub App token, including their lifecycle.
 */
interface GitHubAppTokenService {

    fun getAppInstallationToken(
        configurationName: String,
        appId: String,
        appPrivateKey: String,
        appInstallationAccountName: String?,
    ): String

    fun getAppInstallationTokenInformation(
        configurationName: String,
        appId: String,
        appPrivateKey: String,
        appInstallationAccountName: String?,
    ): GitHubAppToken

    fun invalidateAppInstallationToken(configurationName: String)

    /**
     * Gets the account of the installation used by this app.
     */
    fun getAppInstallationAccount(
        configurationName: String,
        appId: String,
        appPrivateKey: String,
        appInstallationAccountName: String?,
    ): GitHubAppAccount

}
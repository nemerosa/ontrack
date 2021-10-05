package net.nemerosa.ontrack.extension.github.app

/**
 * Service used to link GitHub configurations to their GitHub App token, including their lifecycle.
 */
interface GitHubAppTokenService {

    fun getAppInstallationToken(
        configurationName: String,
        appId: String,
        appPrivateKey: String,
        appInstallationAccountName: String?
    ): String

    fun invalidateAppInstallationToken(configurationName: String)

}
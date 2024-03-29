package net.nemerosa.ontrack.extension.github.app

import net.nemerosa.ontrack.extension.github.app.client.GitHubAppAccount
import net.nemerosa.ontrack.extension.github.app.client.GitHubAppClient
import net.nemerosa.ontrack.model.support.OntrackConfigProperties
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap

@Service
class DefaultGitHubAppTokenService(
    private val gitHubAppClient: GitHubAppClient,
    private val ontrackConfigProperties: OntrackConfigProperties,
) : GitHubAppTokenService {

    /**
     * Using an in-memory cache, indexed by the configuration name.
     */
    private val cache = ConcurrentHashMap<String, GitHubAppToken>()

    override fun invalidateAppInstallationToken(configurationName: String) {
        cache[configurationName]?.invalidate()
    }

    override fun getAppInstallationToken(
        configurationName: String,
        appId: String,
        appPrivateKey: String,
        appInstallationAccountName: String?
    ): String? = gitHubAppToken(configurationName, appId, appPrivateKey, appInstallationAccountName)?.token

    override fun getAppInstallationTokenInformation(
        configurationName: String,
        appId: String,
        appPrivateKey: String,
        appInstallationAccountName: String?
    ): GitHubAppToken? = gitHubAppToken(configurationName, appId, appPrivateKey, appInstallationAccountName)

    override fun getAppInstallationAccount(
        configurationName: String,
        appId: String,
        appPrivateKey: String,
        appInstallationAccountName: String?,
    ): GitHubAppAccount? =
        gitHubAppToken(configurationName, appId, appPrivateKey, appInstallationAccountName)?.installation?.account

    private fun gitHubAppToken(
        configurationName: String,
        appId: String,
        appPrivateKey: String,
        appInstallationAccountName: String?
    ): GitHubAppToken? = cache.compute(configurationName) { _, existingToken: GitHubAppToken? ->
        existingToken
            // If token is defined, checks its validity
            ?.takeIf { it.isValid() }
            // If not defined or not valid, regenerate it
            ?: renewToken(
                appId,
                appPrivateKey,
                appInstallationAccountName
            )
    }

    private fun renewToken(
        appId: String,
        appPrivateKey: String,
        appInstallationAccountName: String?
    ): GitHubAppToken? {
        // If not checking, we don't return any token
        if (!ontrackConfigProperties.configurationTest) {
            return null
        }
        // Gets a client
        val appClient = GitHubApp(gitHubAppClient)
        // Generate JWT token
        val jwt = GitHubApp.generateJWT(appId, appPrivateKey)
        // Getting the installation
        val appInstallationId = appClient.getInstallation(jwt, appId, appInstallationAccountName)
        // Generating & storing the token
        return appClient.generateInstallationToken(jwt, appInstallationId)
    }
}

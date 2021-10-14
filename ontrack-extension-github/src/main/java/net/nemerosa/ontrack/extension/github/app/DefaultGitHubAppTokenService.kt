package net.nemerosa.ontrack.extension.github.app

import net.nemerosa.ontrack.extension.github.app.client.GitHubAppAccount
import net.nemerosa.ontrack.extension.github.app.client.GitHubAppClient
import net.nemerosa.ontrack.model.structure.NameDescription
import net.nemerosa.ontrack.model.support.ApplicationLogEntry
import net.nemerosa.ontrack.model.support.ApplicationLogService
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap

@Service
class DefaultGitHubAppTokenService(
    private val gitHubAppClient: GitHubAppClient,
    private val applicationLogService: ApplicationLogService,
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
    ): GitHubAppAccount? = gitHubAppToken(configurationName, appId, appPrivateKey, appInstallationAccountName)?.installation?.account

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
        val appClient = GitHubApp(gitHubAppClient)
        // Generate JWT token
        val jwt = GitHubApp.generateJWT(appId, appPrivateKey)
        // Getting the installation
        return try {
            val appInstallationId = appClient.getInstallation(jwt, appId, appInstallationAccountName)
            // Generating & storing the token
            appClient.generateInstallationToken(jwt, appInstallationId)
        } catch (any: Exception) {
            // Logging an error
            applicationLogService.log(
                ApplicationLogEntry.error(
                    any,
                    NameDescription.nd("github-token", "Cannot generate GitHub app access token"),
                    "Cannot generate GitHub app access token for app = $appId"
                ).withDetail("app.id", appId).withDetail("app.installation", appInstallationAccountName ?: "")
            )
            // Not returning a token
            null
        }
    }
}

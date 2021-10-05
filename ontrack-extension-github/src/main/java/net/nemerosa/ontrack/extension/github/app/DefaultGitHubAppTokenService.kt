package net.nemerosa.ontrack.extension.github.app

import net.nemerosa.ontrack.extension.github.app.client.GitHubAppClient
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap

@Service
class DefaultGitHubAppTokenService(
    private val gitHubAppClient: GitHubAppClient
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
    ): String {
        return cache.compute(configurationName) { _, existingToken: GitHubAppToken? ->
            existingToken
                // If token is defined, checks its validity
                ?.takeIf { it.isValid() }
                // If not defined or not valid, regenerate it
                ?: renewToken(
                    appId,
                    appPrivateKey,
                    appInstallationAccountName
                )
        }?.token ?: throw GitHubAppNoTokenException(appId)
    }

    private fun renewToken(
        appId: String,
        appPrivateKey: String,
        appInstallationAccountName: String?
    ): GitHubAppToken {
        val appClient = GitHubApp(gitHubAppClient)
        // Generate JWT token
        val jwt = GitHubApp.generateJWT(appId, appPrivateKey)
        // Getting the installation
        val appInstallationId = appClient.getInstallation(jwt, appId, appInstallationAccountName)
        // Generating & storing the token
        return appClient.generateInstallationToken(jwt, appInstallationId)
    }
}

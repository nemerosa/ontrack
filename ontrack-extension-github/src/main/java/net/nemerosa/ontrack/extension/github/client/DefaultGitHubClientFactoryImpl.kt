package net.nemerosa.ontrack.extension.github.client

import net.nemerosa.ontrack.extension.git.GitConfigProperties
import net.nemerosa.ontrack.extension.github.app.GitHubAppTokenService
import net.nemerosa.ontrack.extension.github.model.GitHubEngineConfiguration
import net.nemerosa.ontrack.model.support.ApplicationLogService
import org.springframework.stereotype.Component

@Component
class DefaultGitHubClientFactoryImpl(
    private val gitHubAppTokenService: GitHubAppTokenService,
    private val applicationLogService: ApplicationLogService,
    private val gitConfigProperties: GitConfigProperties,
) : OntrackGitHubClientFactory {
    override fun create(configuration: GitHubEngineConfiguration): OntrackGitHubClient {
        return DefaultOntrackGitHubClient(configuration, gitHubAppTokenService, applicationLogService, gitConfigProperties.sync.timeoutSeconds)
    }
}
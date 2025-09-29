package net.nemerosa.ontrack.extension.github.client

import net.nemerosa.ontrack.extension.git.GitConfigProperties
import net.nemerosa.ontrack.extension.github.app.GitHubAppRateLimitMetrics
import net.nemerosa.ontrack.extension.github.app.GitHubAppTokenService
import net.nemerosa.ontrack.extension.github.model.GitHubEngineConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

@Component
@ConditionalOnProperty(
    name = [OntrackGitHubClient.PROPERTY_GITHUB_CLIENT_TYPE],
    havingValue = OntrackGitHubClient.PROPERTY_GITHUB_CLIENT_TYPE_DEFAULT,
    matchIfMissing = true,
)
class DefaultGitHubClientFactoryImpl(
    private val gitHubAppTokenService: GitHubAppTokenService,
    private val gitConfigProperties: GitConfigProperties,
    private val gitHubAppRateLimitMetrics: GitHubAppRateLimitMetrics,
) : OntrackGitHubClientFactory {
    override fun create(configuration: GitHubEngineConfiguration): OntrackGitHubClient {
        return DefaultOntrackGitHubClient(
            configuration = configuration,
            gitHubAppTokenService = gitHubAppTokenService,
            timeout = gitConfigProperties.remote.timeout,
            retries = gitConfigProperties.remote.retries,
            interval = gitConfigProperties.remote.interval,
            gitHubAppRateLimitMetrics = gitHubAppRateLimitMetrics,
        )
    }
}
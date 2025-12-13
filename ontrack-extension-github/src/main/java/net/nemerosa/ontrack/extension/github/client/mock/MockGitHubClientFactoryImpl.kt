package net.nemerosa.ontrack.extension.github.client.mock

import net.nemerosa.ontrack.extension.github.client.OntrackGitHubClient
import net.nemerosa.ontrack.extension.github.client.OntrackGitHubClientFactory
import net.nemerosa.ontrack.extension.github.model.GitHubEngineConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

@Component
@ConditionalOnProperty(
    name = [OntrackGitHubClient.Companion.PROPERTY_GITHUB_CLIENT_TYPE],
    havingValue = OntrackGitHubClient.Companion.PROPERTY_GITHUB_CLIENT_TYPE_MOCK,
    matchIfMissing = false,
)
class MockGitHubClientFactoryImpl(
) : OntrackGitHubClientFactory {
    override fun create(configuration: GitHubEngineConfiguration): OntrackGitHubClient {
        return MockOntrackGitHubClient(
            configuration = configuration,
        )
    }
}
package net.nemerosa.ontrack.extension.github.client

import net.nemerosa.ontrack.extension.github.app.client.DefaultGitHubAppClient
import net.nemerosa.ontrack.extension.github.app.client.GitHubAppClient
import net.nemerosa.ontrack.extension.github.model.GitHubEngineConfiguration
import org.springframework.stereotype.Component

@Component
class DefaultGitHubClientFactoryImpl : OntrackGitHubClientFactory {
    override fun create(configuration: GitHubEngineConfiguration): OntrackGitHubClient {
        return DefaultOntrackGitHubClient(configuration, DefaultGitHubAppClient())
    }
}
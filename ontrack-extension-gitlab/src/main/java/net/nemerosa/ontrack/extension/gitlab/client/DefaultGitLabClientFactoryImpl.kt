package net.nemerosa.ontrack.extension.gitlab.client

import net.nemerosa.ontrack.extension.gitlab.model.GitLabConfiguration
import org.springframework.stereotype.Component

@Component
class DefaultGitLabClientFactoryImpl : OntrackGitLabClientFactory {
    override fun create(configuration: GitLabConfiguration): OntrackGitLabClient =
            DefaultOntrackGitLabClient(configuration)
}
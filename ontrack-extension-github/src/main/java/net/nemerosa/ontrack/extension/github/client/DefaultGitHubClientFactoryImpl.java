package net.nemerosa.ontrack.extension.github.client;

import net.nemerosa.ontrack.extension.github.model.GitHubEngineConfiguration;
import org.springframework.stereotype.Component;

@Component
public class DefaultGitHubClientFactoryImpl implements OntrackGitHubClientFactory {
    @Override
    public OntrackGitHubClient create(GitHubEngineConfiguration configuration, String repository) {
        return new DefaultOntrackGitHubClient(configuration, repository);
    }
}

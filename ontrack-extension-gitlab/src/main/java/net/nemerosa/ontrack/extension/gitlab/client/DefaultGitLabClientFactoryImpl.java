package net.nemerosa.ontrack.extension.gitlab.client;

import net.nemerosa.ontrack.extension.gitlab.model.GitLabConfiguration;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class DefaultGitLabClientFactoryImpl implements OntrackGitLabClientFactory {
    @Override
    public OntrackGitLabClient create(GitLabConfiguration configuration) {
        try {
            return new DefaultOntrackGitLabClient(configuration);
        } catch (IOException e) {
            throw new OntrackGitLabClientException(e);
        }
    }
}

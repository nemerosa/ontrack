package net.nemerosa.ontrack.extension.gitlab.client;

import net.nemerosa.ontrack.extension.gitlab.model.GitLabConfiguration;
import org.gitlab.api.GitlabAPI;
import org.gitlab.api.TokenType;
import org.gitlab.api.models.GitlabProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class DefaultOntrackGitLabClient implements OntrackGitLabClient {

    private final Logger logger = LoggerFactory.getLogger(OntrackGitLabClient.class);

    private final GitlabAPI api;

    public DefaultOntrackGitLabClient(GitLabConfiguration configuration) throws IOException {
        String personalAccessToken = configuration.getPersonalAccessToken();
        GitlabAPI api = GitlabAPI.connect(
                configuration.getUrl(),
                personalAccessToken,
                TokenType.PRIVATE_TOKEN
        );
        if (configuration.isIgnoreSslCertificate()) {
            this.api = api.ignoreCertificateErrors(true);
        } else {
            this.api = api;
        }
    }

    @Override
    public List<String> getRepositories() {
        logger.debug("[gitlab] Getting repository list");
        try {
            return api.getProjects().stream()
                    .map(GitlabProject::getNameWithNamespace)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new OntrackGitLabClientException(e);
        }
    }

}

package net.nemerosa.ontrack.extension.github.client;

import net.nemerosa.ontrack.common.Time;
import net.nemerosa.ontrack.extension.github.model.*;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.egit.github.core.*;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.client.RequestException;
import org.eclipse.egit.github.core.service.IssueService;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class DefaultOntrackGitHubClient implements OntrackGitHubClient {

    private final Logger logger = LoggerFactory.getLogger(OntrackGitHubClient.class);

    private final GitHubEngineConfiguration configuration;

    public DefaultOntrackGitHubClient(GitHubEngineConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public List<String> getRepositories() {
        // Logging
        logger.debug("[github] Getting repository list");
        // Getting a client
        GitHubClient client = createGitHubClient();
        // Service
        RepositoryService repositoryService = new RepositoryService(client);
        // Gets the repository names
        try {
            return repositoryService.getRepositories().stream()
                    .map(Repository::getName)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new OntrackGitHubClientException(e);
        }
    }

    @Override
    public GitHubIssue getIssue(String repository, int id) {
        // Logging
        logger.debug("[github] Getting issue {}/{}", repository, id);
        // Getting a client
        GitHubClient client = createGitHubClient();
        // Issue service using this client
        IssueService service = new IssueService(client);
        // Gets the repository for this project
        String owner = StringUtils.substringBefore(repository, "/");
        String name = StringUtils.substringAfter(repository, "/");
        Issue issue;
        try {
            issue = service.getIssue(owner, name, id);
        } catch (RequestException ex) {
            if (ex.getStatus() == 404) {
                return null;
            } else {
                throw new OntrackGitHubClientException(ex);
            }
        } catch (IOException e) {
            throw new OntrackGitHubClientException(e);
        }
        // Conversion
        return new GitHubIssue(
                id,
                issue.getHtmlUrl(),
                issue.getTitle(),
                issue.getBodyText(),
                issue.getBodyHtml(),
                toUser(issue.getAssignee()),
                toLabels(issue.getLabels()),
                toState(issue.getState()),
                toMilestone(repository, issue.getMilestone()),
                toDateTime(issue.getCreatedAt()),
                toDateTime(issue.getUpdatedAt()),
                toDateTime(issue.getClosedAt())
        );
    }

    protected GitHubClient createGitHubClient() {
        // GitHub client (non authentified)
        GitHubClient client = new GitHubClient() {
            @Override
            protected HttpURLConnection configureRequest(HttpURLConnection request) {
                HttpURLConnection connection = super.configureRequest(request);
                connection.setRequestProperty(HEADER_ACCEPT, "application/vnd.github.v3.full+json");
                return connection;
            }
        };
        // Authentication
        String oAuth2Token = configuration.getOauth2Token();
        if (StringUtils.isNotBlank(oAuth2Token)) {
            client.setOAuth2Token(oAuth2Token);
        } else {
            String user = configuration.getUser();
            String password = configuration.getPassword();
            if (StringUtils.isNotBlank(user)) {
                client.setCredentials(user, password);
            }
        }
        return client;
    }

    private LocalDateTime toDateTime(Date date) {
        if (date == null) {
            return null;
        } else {
            return Time.from(date, null);
        }
    }

    private GitHubMilestone toMilestone(String repository, Milestone milestone) {
        if (milestone != null) {
            return new GitHubMilestone(
                    milestone.getTitle(),
                    toState(milestone.getState()),
                    milestone.getNumber(),
                    String.format(
                            "%s/%s/issues?milestone=%d&state=open",
                            configuration.getUrl(),
                            repository,
                            milestone.getNumber()
                    )
            );
        } else {
            return null;
        }
    }

    private GitHubState toState(String state) {
        return GitHubState.valueOf(state);
    }

    private List<GitHubLabel> toLabels(List<Label> labels) {
        return labels.stream()
                .map(label -> new GitHubLabel(
                        label.getName(),
                        label.getColor()
                ))
                .collect(Collectors.toList());
    }

    private GitHubUser toUser(User user) {
        if (user == null) {
            return null;
        } else {
            return new GitHubUser(
                    user.getLogin(),
                    user.getHtmlUrl()
            );
        }
    }

}

package net.nemerosa.ontrack.extension.gitlab.client;

import net.nemerosa.ontrack.extension.git.model.GitPullRequest;
import net.nemerosa.ontrack.extension.gitlab.model.GitLabConfiguration;
import net.nemerosa.ontrack.extension.gitlab.model.GitLabIssueWrapper;
import org.gitlab.api.GitlabAPI;
import org.gitlab.api.TokenType;
import org.gitlab.api.models.GitlabIssue;
import org.gitlab.api.models.GitlabMergeRequest;
import org.gitlab.api.models.GitlabMilestone;
import org.gitlab.api.models.GitlabProject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class DefaultOntrackGitLabClient implements OntrackGitLabClient {

    private final Logger logger = LoggerFactory.getLogger(OntrackGitLabClient.class);

    private final GitlabAPI api;
    private final GitLabConfiguration configuration;

    public DefaultOntrackGitLabClient(GitLabConfiguration configuration) {
        this.configuration = configuration;
        String personalAccessToken = configuration.getPassword();
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
        } catch (Exception e) {
            throw new OntrackGitLabClientException(e);
        }
    }

    @Override
    public GitLabIssueWrapper getIssue(String repository, int id) {
        try {
            // Issue
            String issueUrl = configuration.getUrl() + "/" + repository + GitlabIssue.URL + "/" + id;
            GitlabIssue issue = api.getIssue(repository, id);
            // Milestone URL
            String milestoneUrl = null;
            if (issue.getMilestone() != null) {
                milestoneUrl = configuration.getUrl() + "/" + repository + GitlabMilestone.URL + "/" + issue.getMilestone().getId();
            }
            // OK
            return GitLabIssueWrapper.of(issue, milestoneUrl, issueUrl);
        } catch (Exception e) {
            throw new OntrackGitLabClientException(e);
        }
    }


    @Nullable
    @Override
    public GitPullRequest getPullRequest(@NotNull String repository, int id) {
        try {
            try {
                GitlabMergeRequest pr = api.getMergeRequestByIid(repository, id);
                return new GitPullRequest(
                        id,
                        "#" + id,
                        pr.getSourceBranch(),
                        pr.getTargetBranch(),
                        pr.getTitle()
                );
            } catch (FileNotFoundException ignored) {
                return null;
            }
        } catch (IOException e) {
            throw new OntrackGitLabClientException(e);
        }
    }

}

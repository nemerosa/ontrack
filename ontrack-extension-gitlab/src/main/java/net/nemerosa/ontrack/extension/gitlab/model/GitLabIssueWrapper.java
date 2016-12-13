package net.nemerosa.ontrack.extension.gitlab.model;

import lombok.Data;
import net.nemerosa.ontrack.extension.issues.model.Issue;
import net.nemerosa.ontrack.extension.issues.model.IssueStatus;
import org.gitlab.api.models.GitlabIssue;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Data
public class GitLabIssueWrapper implements Issue {

    private final GitlabIssue gitlabIssue;

    @Override
    public String getKey() {
        return String.valueOf(gitlabIssue.getId());
    }

    @Override
    public String getSummary() {
        return gitlabIssue.getTitle();
    }

    @Override
    public String getUrl() {
        // FIXME Method net.nemerosa.ontrack.extension.gitlab.model.GitLabIssueWrapper.getUrl
        return null;
    }

    @Override
    public IssueStatus getStatus() {
        // FIXME Method net.nemerosa.ontrack.extension.gitlab.model.GitLabIssueWrapper.getStatus
        return null;
    }

    @Override
    public LocalDateTime getUpdateTime() {
        // FIXME Method net.nemerosa.ontrack.extension.gitlab.model.GitLabIssueWrapper.getUpdateTime
        return null;
    }

    public static GitLabIssueWrapper of(GitlabIssue issue) {
        return new GitLabIssueWrapper(issue);
    }

    public List<String> getLabels() {
        return Arrays.asList(gitlabIssue.getLabels());
    }
}

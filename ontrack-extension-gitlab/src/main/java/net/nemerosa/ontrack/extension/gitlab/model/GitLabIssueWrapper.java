package net.nemerosa.ontrack.extension.gitlab.model;

import lombok.Data;
import net.nemerosa.ontrack.common.Time;
import net.nemerosa.ontrack.extension.issues.model.Issue;
import net.nemerosa.ontrack.extension.issues.model.IssueStatus;
import org.gitlab.api.models.GitlabIssue;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Data
public class GitLabIssueWrapper implements Issue {

    private final GitlabIssue gitlabIssue;
    private final String milestoneUrl;
    private final String issueUrl;

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
        return issueUrl;
    }

    @Override
    public IssueStatus getStatus() {
        return new GitLabIssueStatusWrapper(gitlabIssue.getState());
    }

    @Override
    public LocalDateTime getUpdateTime() {
        return Time.from(gitlabIssue.getUpdatedAt(), null);
    }

    public static GitLabIssueWrapper of(GitlabIssue issue, String milestoneUrl, String issueUrl) {
        return new GitLabIssueWrapper(issue, milestoneUrl, issueUrl);
    }

    public List<String> getLabels() {
        return Arrays.asList(gitlabIssue.getLabels());
    }
}

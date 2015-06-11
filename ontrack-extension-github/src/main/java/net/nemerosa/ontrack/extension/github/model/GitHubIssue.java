package net.nemerosa.ontrack.extension.github.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import net.nemerosa.ontrack.extension.issues.model.Issue;
import net.nemerosa.ontrack.extension.issues.model.IssueStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class GitHubIssue implements Issue {

    private final int id;
    private final String url;
    private final String title;
    private final String body;
    private final String bodyHtml;
    private final GitHubUser assignee;
    private final List<GitHubLabel> labels;
    private final GitHubState state;
    private final GitHubMilestone milestone;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final LocalDateTime closedAt;

    @Override
    public String getKey() {
        return String.valueOf(id);
    }

    @Override
    public String getDisplayKey() {
        return "#" + id;
    }

    @Override
    public String getSummary() {
        return title;
    }

    @Override
    public IssueStatus getStatus() {
        return new GitHubIssueStatus(state.name());
    }

    @Override
    @JsonIgnore
    public LocalDateTime getUpdateTime() {
        return updatedAt;
    }
}

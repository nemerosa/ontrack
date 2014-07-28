package net.nemerosa.ontrack.extension.github.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class GitHubIssue {

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

}

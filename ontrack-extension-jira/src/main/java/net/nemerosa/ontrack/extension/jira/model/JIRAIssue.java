package net.nemerosa.ontrack.extension.jira.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Wither;
import net.nemerosa.ontrack.extension.issues.model.Issue;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class JIRAIssue implements Issue {

    private final String url;
    private final String key;
    private final String summary;
    private final JIRAStatus status;
    private final String assignee;
    private final LocalDateTime updateTime;
    private final List<JIRAField> fields;
    private final List<JIRAVersion> affectedVersions;
    private final List<JIRAVersion> fixVersions;
    private final String issueType;
    @Wither
    private final List<JIRALink> links;

}

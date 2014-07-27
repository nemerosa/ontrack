package net.nemerosa.ontrack.extension.git.model;

import lombok.Data;
import net.nemerosa.ontrack.extension.issues.model.IssueServiceConfigurationRepresentation;

import java.util.List;

@Data
public class GitChangeLogIssues {

    private final IssueServiceConfigurationRepresentation issueServiceConfiguration;
    private final List<GitChangeLogIssue> list;

}

package net.nemerosa.ontrack.extension.svn.model;

import lombok.Data;
import net.nemerosa.ontrack.extension.issues.model.IssueServiceConfigurationRepresentation;

import java.util.List;

@Data
public class SVNChangeLogIssues {

    private final String allIssuesLink;
    private final IssueServiceConfigurationRepresentation issueServiceConfiguration;
    private final List<SVNChangeLogIssue> list;

}

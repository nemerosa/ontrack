package net.nemerosa.ontrack.extension.github.model;

import lombok.Data;
import net.nemerosa.ontrack.extension.issues.model.IssueStatus;

@Data
public class GitHubIssueStatus implements IssueStatus {

    private final String name;

}

package net.nemerosa.ontrack.extension.jira.model;

import lombok.Data;
import net.nemerosa.ontrack.extension.issues.model.IssueStatus;

@Data
public class JIRAStatus implements IssueStatus {

    private final String name;
    private final String iconUrl;

}

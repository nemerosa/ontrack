package net.nemerosa.ontrack.extension.gitlab.model;

import lombok.Data;
import net.nemerosa.ontrack.extension.issues.model.IssueStatus;

@Data
public class GitLabIssueStatusWrapper implements IssueStatus {

    private final String gitlabStatus;

    @Override
    public String getName() {
        return gitlabStatus;
    }
}

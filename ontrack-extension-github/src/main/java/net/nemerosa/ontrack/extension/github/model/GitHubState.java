package net.nemerosa.ontrack.extension.github.model;

import net.nemerosa.ontrack.extension.issues.model.IssueStatus;

public enum GitHubState implements IssueStatus {

    open, closed;

    @Override
    public String getName() {
        return name();
    }


}

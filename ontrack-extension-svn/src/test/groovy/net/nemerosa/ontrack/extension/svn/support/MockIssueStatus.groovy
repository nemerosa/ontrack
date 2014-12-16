package net.nemerosa.ontrack.extension.svn.support

import net.nemerosa.ontrack.extension.issues.model.IssueStatus

enum MockIssueStatus implements IssueStatus {

    OPEN,

    CLOSED

    @Override
    String getName() {
        name()
    }
}

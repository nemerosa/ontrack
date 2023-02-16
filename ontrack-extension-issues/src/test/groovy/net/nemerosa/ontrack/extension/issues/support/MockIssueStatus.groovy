package net.nemerosa.ontrack.extension.issues.support

import net.nemerosa.ontrack.extension.issues.model.IssueStatus

/**
 * @deprecated Switch to Kotlin
 */
@Deprecated
enum MockIssueStatus implements IssueStatus {

    OPEN,

    CLOSED

    @Override
    String getName() {
        name()
    }
}

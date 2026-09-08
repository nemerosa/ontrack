package net.nemerosa.ontrack.extension.scm.mock

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.extension.issues.model.Issue
import net.nemerosa.ontrack.extension.issues.model.IssueStatus
import java.time.LocalDateTime

data class MockIssue(
    val repositoryName: String,
    override val key: String,
    val message: String,
    val types: Set<String>? = null,
    /**
     * Defaults to the time the issue is registered, and is a parameter only so that a
     * repository restored from a [MockRepositoryData] keeps the times it was registered with
     * instead of dating every issue from the restart.
     */
    override val updateTime: LocalDateTime = Time.now(),
) : Issue {

    val commits = mutableListOf<String>()

    fun addCommitId(id: String) {
        commits.add(id)
    }

    fun lastCommitId() = commits.lastOrNull()

    override val summary: String = message
    override val url: String = "mock://$repositoryName/issue/$key"
    override val status: IssueStatus = MockIssueStatus(name = "open")
}

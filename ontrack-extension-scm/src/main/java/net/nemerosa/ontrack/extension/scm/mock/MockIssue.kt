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
) : Issue {
    override val summary: String = message
    override val url: String = "mock://$repositoryName/issue/$key"
    override val status: IssueStatus = MockIssueStatus(name = "open")
    override val updateTime: LocalDateTime = Time.now()
}
package net.nemerosa.ontrack.extension.issues.mock

import net.nemerosa.ontrack.extension.issues.model.Issue
import java.time.LocalDateTime

data class MockIssue(
    val id: Int,
    override val status: MockIssueStatus = MockIssueStatus.OPEN,
) : Issue {
    override val key: String = id.toString()

    override val displayKey: String = "#$id"
    override val summary: String = "Issue #$id"

    override val url: String = "uri:issue/$id"

    override val updateTime: LocalDateTime = LocalDateTime.of(2014, 12, 10, 8, 32, id % 60)
}
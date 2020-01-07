package net.nemerosa.ontrack.extension.github.model

import net.nemerosa.ontrack.extension.issues.model.Issue
import net.nemerosa.ontrack.extension.issues.model.IssueStatus
import java.time.LocalDateTime

data class GitHubIssue(
        val id: Int,
        override val url: String,
        override val summary: String,
        val body: String,
        val bodyHtml: String,
        val assignee: GitHubUser,
        val labels: List<GitHubLabel>,
        val state: GitHubState,
        val milestone: GitHubMilestone,
        val createdAt: LocalDateTime,
        override val updateTime: LocalDateTime,
        val closedAt: LocalDateTime
) : Issue {

    override val key: String
        get() = id.toString()

    override val displayKey: String
        get() = "#$id"

    override val status: IssueStatus
        get() = GitHubIssueStatus(state.name)

}
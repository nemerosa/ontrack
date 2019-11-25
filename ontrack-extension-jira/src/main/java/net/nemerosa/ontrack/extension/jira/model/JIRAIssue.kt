package net.nemerosa.ontrack.extension.jira.model

import net.nemerosa.ontrack.extension.issues.model.Issue
import java.time.LocalDateTime

class JIRAIssue(
        override val url: String,
        override val key: String,
        override val summary: String,
        override val status: JIRAStatus,
        val assignee: String,
        override val updateTime: LocalDateTime,
        val fields: List<JIRAField>,
        val affectedVersions: List<JIRAVersion>,
        val fixVersions: List<JIRAVersion>,
        val issueType: String,
        val links: List<JIRALink>
) : Issue {
    fun withLinks(links: List<JIRALink>) = JIRAIssue(
            url,
            key,
            summary,
            status,
            assignee,
            updateTime,
            fields,
            affectedVersions,
            fixVersions,
            issueType,
            links
    )
}

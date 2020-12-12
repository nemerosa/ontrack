package net.nemerosa.ontrack.extension.gitlab.model

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.extension.issues.model.Issue
import net.nemerosa.ontrack.extension.issues.model.IssueStatus
import java.time.LocalDateTime

typealias GitLabIssue = org.gitlab4j.api.models.Issue

class GitLabIssueWrapper(
        val gitlabIssue: GitLabIssue,
        val milestoneUrl: String?
) : Issue {

    override val url: String = gitlabIssue.webUrl
    override val key: String = gitlabIssue.id.toString()
    override val displayKey: String = "#${gitlabIssue.id}"
    override val summary: String = gitlabIssue.title
    override val status: IssueStatus = GitLabIssueStatusWrapper(gitlabIssue.state.name)
    override val updateTime: LocalDateTime = Time.from(gitlabIssue.updatedAt.time)
    val labels: List<String> = gitlabIssue.labels

}
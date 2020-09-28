package net.nemerosa.ontrack.extension.gitlab.model

import net.nemerosa.ontrack.extension.issues.model.IssueStatus

data class GitLabIssueStatusWrapper(override val name: String) : IssueStatus

package net.nemerosa.ontrack.extension.github.model

import net.nemerosa.ontrack.extension.issues.model.IssueStatus

data class GitHubIssueStatus(
        override val name: String
) : IssueStatus

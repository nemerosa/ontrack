package net.nemerosa.ontrack.extension.jira.model

import net.nemerosa.ontrack.extension.issues.model.IssueStatus

data class JIRAStatus(
        override val name: String,
        val iconUrl: String
) : IssueStatus

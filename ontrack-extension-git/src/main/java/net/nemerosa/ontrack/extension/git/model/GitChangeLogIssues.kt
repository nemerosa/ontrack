package net.nemerosa.ontrack.extension.git.model

import net.nemerosa.ontrack.extension.issues.model.IssueServiceConfigurationRepresentation

class GitChangeLogIssues(
        val issueServiceConfiguration: IssueServiceConfigurationRepresentation,
        val list: List<GitChangeLogIssue>
)

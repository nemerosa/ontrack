package net.nemerosa.ontrack.extension.svn.model

import net.nemerosa.ontrack.extension.issues.model.IssueServiceConfigurationRepresentation

class SVNChangeLogIssues(
        val allIssuesLink: String?,
        val issueServiceConfiguration: IssueServiceConfigurationRepresentation?,
        val list: List<SVNChangeLogIssue>
)

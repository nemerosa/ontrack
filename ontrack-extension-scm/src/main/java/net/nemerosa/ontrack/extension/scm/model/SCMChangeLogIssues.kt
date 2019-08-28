package net.nemerosa.ontrack.extension.scm.model

import net.nemerosa.ontrack.extension.issues.model.IssueServiceConfigurationRepresentation

class SCMChangeLogIssues<T : SCMChangeLogIssue>(
        val issueServiceConfiguration: IssueServiceConfigurationRepresentation?,
        val list: List<T>
)

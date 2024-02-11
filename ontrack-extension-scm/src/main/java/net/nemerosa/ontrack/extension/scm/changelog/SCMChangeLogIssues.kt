package net.nemerosa.ontrack.extension.scm.changelog

import net.nemerosa.ontrack.extension.issues.model.Issue
import net.nemerosa.ontrack.extension.issues.model.IssueServiceConfigurationRepresentation

data class SCMChangeLogIssues(
    val issueServiceConfiguration: IssueServiceConfigurationRepresentation,
    val issues: List<Issue>,
)

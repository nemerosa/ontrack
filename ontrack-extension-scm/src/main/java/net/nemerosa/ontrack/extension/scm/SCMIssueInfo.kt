package net.nemerosa.ontrack.extension.scm

import net.nemerosa.ontrack.extension.issues.model.Issue
import net.nemerosa.ontrack.extension.issues.model.IssueServiceConfigurationRepresentation

data class SCMIssueInfo(
    val issueServiceConfigurationRepresentation: IssueServiceConfigurationRepresentation,
    val issue: Issue,
    val scmCommitInfo: SCMCommitInfo?,
)
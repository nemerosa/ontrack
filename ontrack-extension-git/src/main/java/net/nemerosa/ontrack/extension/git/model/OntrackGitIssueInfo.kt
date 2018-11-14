package net.nemerosa.ontrack.extension.git.model

import net.nemerosa.ontrack.extension.issues.model.Issue
import net.nemerosa.ontrack.extension.issues.model.IssueServiceConfigurationRepresentation

/**
 * Data that can be collected around an issue.
 */
class OntrackGitIssueInfo(
        /**
         * Associated issue configuration
         */
        val issueServiceConfigurationRepresentation: IssueServiceConfigurationRepresentation,
        /**
         * Associated issue
         */
        val issue: Issue,
        /**
         * Last commit per branch
         */
        val commitInfos: List<OntrackGitIssueCommitInfo>
)


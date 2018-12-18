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
         * Commit information
         */
        val commitInfo: OntrackGitCommitInfo?
) {
    fun first() = OntrackGitIssueInfo(
            issueServiceConfigurationRepresentation,
            issue,
            commitInfo?.first()
    )
}


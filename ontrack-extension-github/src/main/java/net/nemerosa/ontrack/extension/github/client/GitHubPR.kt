package net.nemerosa.ontrack.extension.github.client

import com.fasterxml.jackson.annotation.JsonIgnore
import net.nemerosa.ontrack.extension.scm.service.SCMPullRequestStatus

data class GitHubPR(
    /**
     * Local ID of the PR
     */
    val number: Int,
    /**
     * Merged status
     */
    val merged: Boolean,
    /**
     * State
     */
    val state: String,
    /**
     * Is the PR mergeable?
     */
    val mergeable: Boolean?,
    /**
     * Mergeable status
     */
    val mergeable_state: String?,
    /**
     * Link to the PR
     */
    val html_url: String?
) {
    @JsonIgnore
    val status: SCMPullRequestStatus = when (state) {
        "open" -> SCMPullRequestStatus.OPEN
        "closed" -> when (merged) {
            true -> SCMPullRequestStatus.MERGED
            false -> SCMPullRequestStatus.DECLINED
        }

        else -> SCMPullRequestStatus.UNKNOWN
    }
}
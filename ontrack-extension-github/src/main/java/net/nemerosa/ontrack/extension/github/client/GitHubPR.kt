package net.nemerosa.ontrack.extension.github.client

data class GitHubPR(
    /**
     * Local ID of the PR
     */
    val number: Int,
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
)
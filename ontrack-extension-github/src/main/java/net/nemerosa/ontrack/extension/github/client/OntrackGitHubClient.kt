package net.nemerosa.ontrack.extension.github.client

import net.nemerosa.ontrack.extension.github.model.GitHubIssue

/**
 * Client used to connect to a GitHub engine from Ontrack.
 */
interface OntrackGitHubClient {
    /**
     * Gets an issue from a repository.
     *
     * @param repository Repository name, like `nemerosa/ontrack`
     * @param id         ID of the issue
     * @return Details about the issue
     */
    fun getIssue(repository: String, id: Int): GitHubIssue?

    /**
     * Gets the list of repositories available using this client.
     */
    val repositories: List<String>
}
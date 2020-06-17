package net.nemerosa.ontrack.extension.github.client

import net.nemerosa.ontrack.extension.github.model.GitHubIssue
import net.nemerosa.ontrack.extension.github.model.GitHubUser
import org.eclipse.egit.github.core.client.GitHubClient

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

    /**
     * Gets the list of organizations available from this client.
     */
    val organizations: List<GitHubUser>

    /**
     * Gets the list of repositories for an organization
     *
     * @param organization Organization name
     * @return List of repository names in this [organization]
     */
    fun findRepositoriesByOrganization(organization: String): List<String>

    /**
     * Gets the underlying / native GitHub client so that extensions
     * can add features.
     */
    fun createGitHubClient(): GitHubClient
}
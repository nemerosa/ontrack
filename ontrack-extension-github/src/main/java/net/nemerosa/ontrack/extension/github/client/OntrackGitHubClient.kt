package net.nemerosa.ontrack.extension.github.client

import net.nemerosa.ontrack.extension.git.model.GitPullRequest
import net.nemerosa.ontrack.extension.github.model.*
import org.eclipse.egit.github.core.client.GitHubClient
import org.springframework.web.client.RestTemplate


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
     * Gets the list of organizations available from this client.
     */
    val organizations: List<GitHubUser>

    /**
     * Gets the list of repositories for an organization
     *
     * @param organization Organization name
     * @return List of repository in this [organization]
     */
    fun findRepositoriesByOrganization(organization: String): List<GitHubRepository>

    /**
     * Gets the underlying / native GitHub client so that extensions
     * can add features.
     */
    @Deprecated("Prefer using the RestTemplate")
    fun createGitHubClient(): GitHubClient

    /**
     * Creates a [RestTemplate] for accessing GitHub.
     */
    fun createGitHubRestTemplate(): RestTemplate

    /**
     * Gets a pull request using its ID
     *
     * @param repository Repository name, like `nemerosa/ontrack`
     * @param id         ID of the pull request
     * @return Details of the pull request or `null` if it does not exist
     */
    fun getPullRequest(repository: String, id: Int): GitPullRequest?

    /**
     * Gets the list of teams for a repository
     *
     * @param repo Repository (org/name)
     * @return List of teams or `null` if the teams cannot be accessed
     */
    fun getRepositoryTeams(repo: String): List<GitHubTeam>?

    /**
     * Gets the list of teams for this organization.
     *
     * The `read:org` permission is required (it not granted, `null` will be returned).
     *
     * @param login Login for the organization.
     * @return List of teams or null if the teams cannot be accessed.
     */
    fun getOrganizationTeams(login: String): List<GitHubTeam>?

    /**
     * Returns the list of repositories associated to a team.
     *
     * The `read:org` permission is required (it not granted, `null` will be returned).
     *
     * @param login Login for the organization.
     * @param teamSlug Slug of the team
     * @return List of repositories with their permissions or null if the permissions cannot be accessed.
     */
    fun getTeamRepositories(login: String, teamSlug: String): List<GitHubTeamRepository>?
}
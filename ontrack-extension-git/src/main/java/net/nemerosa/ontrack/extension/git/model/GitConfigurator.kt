package net.nemerosa.ontrack.extension.git.model

import net.nemerosa.ontrack.model.structure.Project

/**
 * Extracting the Git configuration from a project.
 */
interface GitConfigurator {

    /**
     * Checks if the project is configured for Git.
     */
    fun isProjectConfigured(project: Project): Boolean

    /**
     * Gets the configuration for a project, when it exists.
     */
    fun getConfiguration(project: Project): GitConfiguration?

    /**
     * Converts a PR key to an ID when possible
     */
    fun toPullRequestID(key: String): Int? = TODO("To be implemented by all Git providers")

    /**
     * Loads a pull request
     *
     * @param configuration Configuration to use
     * @param id            ID of the pull request
     * @return Pull request or null if not existing
     */
    fun getPullRequest(configuration: GitConfiguration, id: Int): GitPullRequest? = TODO("To be implemented by all Git providers")

}
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
    fun toPullRequestID(key: String): Int? {
        if (key.isNotBlank()) {
            val m = "PR-(\\d+)".toRegex().matchEntire(key)
            if (m != null) {
                return m.groupValues[1].toInt(10)
            }
        }
        return null
    }

    /**
     * Given a PR ID, returns a display key.
     */
    fun toPullRequestKey(prId: Int): String = "#$prId"

    /**
     * Loads a pull request
     *
     * @param configuration Configuration to use
     * @param id            ID of the pull request
     * @return Pull request or null if not existing
     */
    fun getPullRequest(configuration: GitConfiguration, id: Int): GitPullRequest?

}
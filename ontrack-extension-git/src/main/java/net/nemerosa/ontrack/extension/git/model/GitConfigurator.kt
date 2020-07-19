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

}
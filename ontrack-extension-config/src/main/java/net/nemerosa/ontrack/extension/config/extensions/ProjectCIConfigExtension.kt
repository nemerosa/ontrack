package net.nemerosa.ontrack.extension.config.extensions

import net.nemerosa.ontrack.extension.config.model.ProjectConfiguration
import net.nemerosa.ontrack.model.extension.Extension
import net.nemerosa.ontrack.model.structure.Project

/**
 * Extension which contributes to the configuration of a project.
 */
interface ProjectCIConfigExtension : Extension {

    /**
     * Configures the project
     */
    fun configureProject(project: Project, configuration: ProjectConfiguration)

}
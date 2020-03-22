package net.nemerosa.ontrack.extension.git.model

import net.nemerosa.ontrack.model.structure.Project
import java.util.*

/**
 * Extracting the Git configuration from a project.
 */
interface GitConfigurator {

    fun isProjectConfigured(project: Project): Boolean

    fun getConfiguration(project: Project): Optional<GitConfiguration>
    
}
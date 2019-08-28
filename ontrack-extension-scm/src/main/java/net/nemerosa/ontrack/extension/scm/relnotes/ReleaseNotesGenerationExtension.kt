package net.nemerosa.ontrack.extension.scm.relnotes

import net.nemerosa.ontrack.model.extension.Extension
import net.nemerosa.ontrack.model.structure.Project

/**
 * This extension point allows an extension to participate into the generation of release notes.
 */
interface ReleaseNotesGenerationExtension : Extension {

    /**
     * Checks if this extension applies to the given project
     */
    fun appliesForProject(project: Project): Boolean

}
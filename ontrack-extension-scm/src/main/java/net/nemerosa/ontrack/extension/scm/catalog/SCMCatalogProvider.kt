package net.nemerosa.ontrack.extension.scm.catalog

import net.nemerosa.ontrack.model.structure.Project

interface SCMCatalogProvider {

    val id: String
    val entries: List<SCMCatalogSource>

    /**
     * Checks if the given catalog entry matches the project.
     */
    fun matches(entry: SCMCatalogEntry, project: Project): Boolean

    /**
     * Gets the SCM location for a project
     */
    fun getSCMLocation(project: Project): SCMLocation?

}
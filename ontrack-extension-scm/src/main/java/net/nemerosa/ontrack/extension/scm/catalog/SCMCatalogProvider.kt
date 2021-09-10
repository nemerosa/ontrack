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
     * Given a SCM repository name, returns a name which is suitable for Ontrack as a project name
     */
    fun toProjectName(scmRepository: String): String

    /**
     * Sets a property on a project so that it is linked to the SCM catalog [entry].
     */
    fun linkProjectToSCM(project: Project, entry: SCMCatalogEntry): Boolean

}
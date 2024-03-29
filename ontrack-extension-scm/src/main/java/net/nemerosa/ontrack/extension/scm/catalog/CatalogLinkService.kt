package net.nemerosa.ontrack.extension.scm.catalog

import net.nemerosa.ontrack.model.structure.Project

interface CatalogLinkService {

    /**
     * Collects all SCM catalog entry links to the projects.
     */
    fun computeCatalogLinks()

    /**
     * Gets the SCM catalog entry linked to a project
     */
    fun getSCMCatalogEntry(project: Project): SCMCatalogEntry?

    /**
     * Gets the project linked to a SCM catalog entry
     */
    fun getLinkedProject(entry: SCMCatalogEntry): Project?

    /**
     * Quick check for checking if an [entry] is linked to a project.
     */
    fun isLinked(entry: SCMCatalogEntry): Boolean

    /**
     * Quick check for checking if a [project] is linked to a SCM catalog entry.
     */
    fun isOrphan(project: Project): Boolean

    /**
     * Stores a link between a project and a SCM catalog entry
     */
    fun storeLink(project: Project, entry: SCMCatalogEntry)

}
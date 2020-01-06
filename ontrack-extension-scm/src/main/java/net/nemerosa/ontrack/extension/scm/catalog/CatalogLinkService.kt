package net.nemerosa.ontrack.extension.scm.catalog

import net.nemerosa.ontrack.model.structure.Project

interface CatalogLinkService {

    fun computeCatalogLinks()

    fun getSCMCatalogEntry(project: Project): SCMCatalogEntry?

    fun getLinkedProject(entry: SCMCatalogEntry): Project?

    /**
     * Quick check for checking if an [entry] is linked to a project.
     */
    fun isLinked(entry: SCMCatalogEntry): Boolean

}
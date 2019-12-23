package net.nemerosa.ontrack.extension.scm.catalog

import net.nemerosa.ontrack.model.structure.Project

interface CatalogLinkService {

    fun computeCatalogLinks()

    fun getSCMCatalogEntry(project: Project): SCMCatalogEntry?

    fun getLinkedProject(entry: SCMCatalogEntry): Project?

}
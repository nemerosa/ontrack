package net.nemerosa.ontrack.extension.scm.catalog

import net.nemerosa.ontrack.model.structure.Project

/**
 * Service used to collect and access information stored for catalog entries
 */
interface CatalogInfoCollector {

    fun collectCatalogInfo(project: Project, logger: (String) -> Unit)

}
package net.nemerosa.ontrack.extension.scm.catalog

import net.nemerosa.ontrack.extension.api.ExtensionManager
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.repository.support.store.EntityDataStore
import net.nemerosa.ontrack.repository.support.store.EntityDataStoreFilter
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional
class CatalogInfoCollectorImpl(
        private val catalogLinkService: CatalogLinkService,
        private val extensionManager: ExtensionManager,
        private val entityDataStore: EntityDataStore,
        private val securityService: SecurityService
) : CatalogInfoCollector {

    override fun collectCatalogInfo(project: Project, logger: (String) -> Unit) {
        logger("Catalog info for ${project.name}")
        val entry = catalogLinkService.getSCMCatalogEntry(project)
        if (entry != null) {
            logger("Catalog info for ${project.name} linked with ${entry.key}")
            val contributors: Collection<CatalogInfoContributor<*>> = extensionManager.getExtensions(CatalogInfoContributor::class.java)
            contributors.forEach { collectCatalogInfo(project, entry, it, logger) }
        } else {
            logger("Deleting catalog info for ${project.name} because no associated SCM catalog entry")
            entityDataStore.deleteByFilter(
                    EntityDataStoreFilter(
                            entity = project,
                            category = STORE_CATEGORY
                    )
            )
        }
    }

    private fun <T> collectCatalogInfo(
            project: Project,
            entry: SCMCatalogEntry,
            contributor: CatalogInfoContributor<T>,
            logger: (String) -> Unit
    ) {
        logger("Catalog info for ${project.name} linked with ${entry.key} by ${contributor.javaClass.name}")
        // Gets the information for this project & this entry
        val info = contributor.collectInfo(project, entry)
        // Storing the information as JSON
        if (info != null) {
            logger("Storing catalog info for ${project.name} linked with ${entry.key} by ${contributor.javaClass.name}")
            entityDataStore.add(
                    project,
                    STORE_CATEGORY,
                    contributor.id,
                    securityService.currentSignature,
                    null,
                    contributor.asJson(info)
            )
        } else {
            logger("Deleting catalog info for ${project.name} linked with ${entry.key} by ${contributor.javaClass.name}")
            entityDataStore.deleteByFilter(
                    EntityDataStoreFilter(
                            entity = project,
                            category = STORE_CATEGORY,
                            name = contributor.id
                    )
            )
        }
    }

    companion object {
        val STORE_CATEGORY: String = CatalogInfoCollector::class.java.name
    }

}
package net.nemerosa.ontrack.extension.scm.catalog

import net.nemerosa.ontrack.extension.api.ExtensionManager
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.NameDescription
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.support.ApplicationLogEntry
import net.nemerosa.ontrack.model.support.ApplicationLogService
import net.nemerosa.ontrack.repository.support.store.EntityDataStore
import net.nemerosa.ontrack.repository.support.store.EntityDataStoreFilter
import net.nemerosa.ontrack.repository.support.store.EntityDataStoreRecord
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional
class CatalogInfoCollectorImpl(
        private val catalogLinkService: CatalogLinkService,
        private val extensionManager: ExtensionManager,
        private val entityDataStore: EntityDataStore,
        private val securityService: SecurityService,
        private val applicationLogService: ApplicationLogService
) : CatalogInfoCollector {

    val contributors: Map<String, CatalogInfoContributor<*>> by lazy {
        extensionManager.getExtensions(CatalogInfoContributor::class.java).associateBy { it.id }
    }

    override fun collectCatalogInfo(project: Project, logger: (String) -> Unit) {
        logger("Catalog info for ${project.name}")
        val entry = catalogLinkService.getSCMCatalogEntry(project)
        if (entry != null) {
            logger("Catalog info for ${project.name} linked with ${entry.key}")
            val contributors: Collection<CatalogInfoContributor<*>> = contributors.values
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

    override fun getCatalogInfos(project: Project): List<CatalogInfo<*>> =
            entityDataStore.getByFilter(
                    EntityDataStoreFilter(
                            entity = project,
                            category = STORE_CATEGORY
                    )
            ).mapNotNull { toCatalogInfo<Any>(it) }

    private fun <T> toCatalogInfo(record: EntityDataStoreRecord): CatalogInfo<T>? {
        @Suppress("UNCHECKED_CAST")
        val extension: CatalogInfoContributor<T>? = contributors[record.name] as CatalogInfoContributor<T>?
        return extension?.run {
            CatalogInfo(
                    collector = extension,
                    data = extension.fromJson(record.data),
                    timestamp = record.signature.time
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
        try {
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
        } catch (ex: Exception) {
            applicationLogService.log(
                    ApplicationLogEntry.error(
                            ex,
                            NameDescription.nd("scm-catalog-info", "Catalog info collection error"),
                            "Error when collecting catalog info for ${project.name} linked with ${entry.key} by ${contributor.javaClass.name}"
                    ).withDetail(
                            "project", project.name
                    ).withDetail(
                            "entry", entry.key
                    ).withDetail(
                            "contributor", contributor.id
                    )
            )
        }
    }

    companion object {
        val STORE_CATEGORY: String = CatalogInfoCollector::class.java.name
    }

}
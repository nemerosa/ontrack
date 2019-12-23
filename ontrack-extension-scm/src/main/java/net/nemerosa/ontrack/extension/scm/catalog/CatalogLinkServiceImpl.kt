package net.nemerosa.ontrack.extension.scm.catalog

import net.nemerosa.ontrack.model.structure.EntityDataService
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.StructureService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class CatalogLinkServiceImpl(
        private val scmCatalog: SCMCatalog,
        private val scmCatalogProviders: List<SCMCatalogProvider>,
        private val structureService: StructureService,
        private val entityDataService: EntityDataService
) : CatalogLinkService {

    private val logger: Logger = LoggerFactory.getLogger(CatalogLinkService::class.java)

    override fun computeCatalogLinks() {
        val projects = structureService.projectList
        val providers = scmCatalogProviders.associateBy { it.id }
        val catalogEntries = scmCatalog.catalogEntries
        val leftOverKeys = catalogEntries.map { it.key }.toMutableSet()
        catalogEntries.forEach {
            if (computeCatalogLink(it, projects, providers)) {
                leftOverKeys.remove(it.key)
            }
        }
        // Cleanup
        projects.forEach { project ->
            val value = entityDataService.retrieve(project, CatalogLinkService::class.java.name)
            if (!value.isNullOrBlank() && value in leftOverKeys) {
                entityDataService.delete(project, value)
            }
        }
    }

    private fun computeCatalogLink(
            entry: SCMCatalogEntry,
            projects: List<Project>,
            providers: Map<String, SCMCatalogProvider>
    ): Boolean {
        logger.debug("Catalog link for ${entry.key}")
        // Gets a provider for this entry
        val provider = providers[entry.scm]
        // For all projects
        if (provider != null) {
            projects.forEach { project ->
                // Is that a match?
                if (provider.matches(entry, project)) {
                    logger.debug("Catalog entry ${entry.key} links with ${project.name}")
                    // Stores the link
                    entityDataService.store(
                            project,
                            CatalogLinkService::class.java.name,
                            entry.key
                    )
                    // OK
                    return true
                }
            }
        }
        // Not linked
        return false
    }

}
package net.nemerosa.ontrack.extension.scm.catalog.sync

import net.nemerosa.ontrack.extension.scm.catalog.*
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.model.structure.NameDescription.Companion.nd
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.StructureService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.jvm.optionals.getOrNull

@Service
@Transactional
class SCMCatalogImportServiceImpl(
    private val cachedSettingsService: CachedSettingsService,
    private val scmCatalogFilterService: SCMCatalogFilterService,
    private val structureService: StructureService,
    private val catalogLinkService: CatalogLinkService,
    scmCatalogProviders: List<SCMCatalogProvider>,
): SCMCatalogImportService {

    private val scmCatalogProvidersIndex = scmCatalogProviders.associateBy { it.id }

    override fun importCatalog(logger: (String) -> Unit) {
        val settings = cachedSettingsService.getCachedSettings(SCMCatalogSyncSettings::class.java)
        if (settings.syncEnabled) {
            // Creates the SCM catalog filter
            val filter = SCMCatalogProjectFilter(
                size = Int.MAX_VALUE,
                scm = settings.scm?.takeIf { it.isNotBlank() },
                config = settings.config?.takeIf { it.isNotBlank() },
                repository = settings.repository?.takeIf { it.isNotBlank() },
                link = SCMCatalogProjectFilterLink.UNLINKED,
            )
            // Gets the list of SCM catalog entries
            logger("Getting the list of unlinked SCM catalog entries")
            val items = scmCatalogFilterService.findCatalogProjectEntries(filter)
            logger("Count of unlinked SCM catalog entries: $")
            // For each unlinked item, create the Ontrack project
            items.forEach {
                createProject(it, logger)
            }
        }
    }

    private fun createProject(item: SCMCatalogEntryOrProject, logger: (String) -> Unit) {
        if (item.project == null && item.entry != null) {
            // Gets the associated SCM provider
            val provider = scmCatalogProvidersIndex[item.entry.scm]
            if (provider != null) {
                // Adapt the name for Ontrack convention
                val name = provider.toProjectName(item.entry.repository)
                // Gets any existing project with this name
                val project = structureService.findProjectByName(name).getOrNull()
                if (project == null) {
                    if (name.length > Project.PROJECT_NAME_MAX_LENGTH) {
                        logger("Cannot import $name project because its length is > ${Project.PROJECT_NAME_MAX_LENGTH}")
                    } else {
                        // Description
                        val description = "This project was automatically created from the SCM entry ${item.entry.scm}/${item.entry.config}/${item.entry.repository}"
                        // Creating the project
                        val createdProject = structureService.newProject(Project.of(nd(name, description)))
                        // Set the SCM property to link to the SCM entry
                        provider.linkProjectToSCM(createdProject, item.entry)
                        // Update the SCM catalog entry to link it to the created project
                        catalogLinkService.storeLink(createdProject, item.entry)
                        // OK
                        logger("Created project $name (id = ${createdProject.id}) from SCM catalog")
                    }
                }
            }
        }
    }

}
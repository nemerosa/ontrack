package net.nemerosa.ontrack.extension.scm.catalog.sync

import net.nemerosa.ontrack.common.getOrNull
import net.nemerosa.ontrack.extension.scm.catalog.SCMCatalogEntryOrProject
import net.nemerosa.ontrack.extension.scm.catalog.SCMCatalogFilterService
import net.nemerosa.ontrack.extension.scm.catalog.SCMCatalogProjectFilter
import net.nemerosa.ontrack.extension.scm.catalog.SCMCatalogProjectFilterLink
import net.nemerosa.ontrack.model.settings.CachedSettingsService
import net.nemerosa.ontrack.model.structure.NameDescription.Companion.nd
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.StructureService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class SCMCatalogImportServiceImpl(
    private val cachedSettingsService: CachedSettingsService,
    private val scmCatalogFilterService: SCMCatalogFilterService,
    private val structureService: StructureService,
): SCMCatalogImportService {

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
            val name = item.entry.repository
            val project = structureService.findProjectByName(name).getOrNull()
            if (project == null) {
                val createdProject = structureService.newProject(Project.of(nd(name, "")))
                logger("Created project $name from SCM catalog")
            }
        }
    }

}
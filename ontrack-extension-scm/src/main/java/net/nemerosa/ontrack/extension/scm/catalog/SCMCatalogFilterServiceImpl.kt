package net.nemerosa.ontrack.extension.scm.catalog

import net.nemerosa.ontrack.common.and
import net.nemerosa.ontrack.model.security.ProjectView
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.security.callAsAdmin
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.StructureService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class SCMCatalogFilterServiceImpl(
        private val scmCatalog: SCMCatalog,
        private val catalogLinkService: CatalogLinkService,
        private val structureService: StructureService,
        private val securityService: SecurityService
) : SCMCatalogFilterService {

    override fun findCatalogEntries(filter: SCMCatalogFilter): List<SCMCatalogEntry> {
        return findCatalogProjectEntries(
                SCMCatalogProjectFilter(
                        offset = filter.offset,
                        size = filter.size,
                        scm = filter.scm,
                        config = filter.config,
                        repository = filter.repository,
                        project = null,
                        link = when (filter.link) {
                            SCMCatalogFilterLink.ALL -> SCMCatalogProjectFilterLink.ALL
                            SCMCatalogFilterLink.LINKED -> SCMCatalogProjectFilterLink.LINKED
                            SCMCatalogFilterLink.UNLINKED -> SCMCatalogProjectFilterLink.UNLINKED
                        }
                )
        ).mapNotNull { it.entry }
    }

    override fun findCatalogProjectEntries(filter: SCMCatalogProjectFilter): List<SCMCatalogEntryOrProject> {
        securityService.checkGlobalFunction(SCMCatalogAccessFunction::class.java)

        // SCM catalog entry filter
        val repositoryRegex = filter.repository?.takeIf { it.isNotBlank() }?.toRegex()
        val entryScmFilter: (SCMCatalogEntry) -> Boolean = filter.scm?.takeIf { it.isNotBlank() }?.let {
            { entry: SCMCatalogEntry -> entry.scm == it }
        } ?: { true }
        val entryConfigFilter: (SCMCatalogEntry) -> Boolean = filter.config?.takeIf { it.isNotBlank() }?.let {
            { entry: SCMCatalogEntry -> entry.config == it }
        } ?: { true }
        val entryRepositoryFilter: (SCMCatalogEntry) -> Boolean = repositoryRegex?.let {
            { entry: SCMCatalogEntry -> it.matches(entry.repository) }
        } ?: { true }
        val entryLinkFilter: (SCMCatalogEntry) -> Boolean = { entry ->
            when (filter.link) {
                SCMCatalogProjectFilterLink.ALL -> true
                SCMCatalogProjectFilterLink.LINKED -> catalogLinkService.isLinked(entry)
                SCMCatalogProjectFilterLink.UNLINKED -> !catalogLinkService.isLinked(entry)
                SCMCatalogProjectFilterLink.ORPHAN -> false
            }
        }
        val entryFilter: (SCMCatalogEntry) -> Boolean = entryScmFilter and
                entryConfigFilter and
                entryRepositoryFilter and
                entryLinkFilter

        val entries: () -> Sequence<SCMCatalogEntryOrProject> = {
            scmCatalog.catalogEntries.filter(entryFilter).map { entry ->
                SCMCatalogEntryOrProject.entry(entry, catalogLinkService.getLinkedProject(entry))
            }
        }

        // Orphan project filter
        val projectRegex = filter.project?.takeIf { it.isNotBlank() }?.toRegex()
        val projectOrphanProject: (Project) -> Boolean = { project ->
            catalogLinkService.isOrphan(project)
        }
        val projectRegexFilter: (Project) -> Boolean = projectRegex?.let {
            { project: Project -> it.matches(project.name) }
        } ?: { true }
        val projectSecurityFilter: (Project) -> Boolean = { project ->
            securityService.isProjectFunctionGranted(project, ProjectView::class.java)
        }
        val projectFilter: (Project) -> Boolean = projectOrphanProject and projectRegexFilter and projectSecurityFilter

        val orphanProjects: () -> Sequence<SCMCatalogEntryOrProject> = {
            securityService.callAsAdmin {
                structureService.projectList
            }.asSequence().filter(projectFilter).map { project ->
                SCMCatalogEntryOrProject.orphanProject(project)
            }
        }

        // ALL --> filtered entries + orphan projects
        // LINKED, UNLINKED --> filtered entries
        // ORPHAN --> orphan projects only

        val allEntries: Sequence<SCMCatalogEntryOrProject> = when (filter.link) {
            SCMCatalogProjectFilterLink.ALL -> entries() + orphanProjects()
            SCMCatalogProjectFilterLink.LINKED -> entries()
            SCMCatalogProjectFilterLink.UNLINKED -> entries()
            SCMCatalogProjectFilterLink.ORPHAN -> orphanProjects()
        }

        // Sorting
        return allEntries.sorted().drop(filter.offset).take(filter.size).toList()
    }

}
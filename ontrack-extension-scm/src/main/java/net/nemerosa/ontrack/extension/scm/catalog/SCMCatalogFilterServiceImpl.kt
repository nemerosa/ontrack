package net.nemerosa.ontrack.extension.scm.catalog

import net.nemerosa.ontrack.common.and
import net.nemerosa.ontrack.model.security.ProjectView
import net.nemerosa.ontrack.model.security.SecurityService
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

    override fun indexCatalogProjectEntries(): Map<SCMCatalogProjectFilterLink, Int> {
        val results = mutableMapOf<SCMCatalogProjectFilterLink, Int>()

        scmCatalog.catalogEntries.forEach { entry ->
            incr(results, SCMCatalogProjectFilterLink.ALL)
            incr(results, SCMCatalogProjectFilterLink.ENTRY)
            val linked = catalogLinkService.isLinked(entry)
            if (linked) {
                incr(results, SCMCatalogProjectFilterLink.LINKED)
            } else {
                incr(results, SCMCatalogProjectFilterLink.UNLINKED)
            }
        }

        securityService.asAdmin {
            structureService.projectList.forEach { project ->
                if (catalogLinkService.isOrphan(project)) {
                    incr(results, SCMCatalogProjectFilterLink.ALL)
                    incr(results, SCMCatalogProjectFilterLink.ORPHAN)
                }
            }
        }

        return results.toMap()
    }

    private fun incr(results: MutableMap<SCMCatalogProjectFilterLink, Int>, link: SCMCatalogProjectFilterLink) {
        results[link] = results[link]?.plus(1) ?: 1
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
        val entryLinkFilter: (SCMCatalogEntry) -> Boolean = getEntryLinkFilter(filter.link)
        val entryBeforeLastActivityFilter: (SCMCatalogEntry) -> Boolean = filter.beforeLastActivity?.let {
            { entry: SCMCatalogEntry -> entry.lastActivity != null && entry.lastActivity.toLocalDate() <= it }
        } ?: { true }
        val entryAfterLastActivityFilter: (SCMCatalogEntry) -> Boolean = filter.afterLastActivity?.let {
            { entry: SCMCatalogEntry -> entry.lastActivity != null && entry.lastActivity.toLocalDate() >= it }
        } ?: { true }
        val entryBeforeCreatedAtFilter: (SCMCatalogEntry) -> Boolean = filter.beforeCreatedAt?.let {
            { entry: SCMCatalogEntry -> entry.createdAt != null && entry.createdAt.toLocalDate() <= it }
        } ?: { true }
        val entryAfterCreatedAtFilter: (SCMCatalogEntry) -> Boolean = filter.afterCreatedAt?.let {
            { entry: SCMCatalogEntry -> entry.createdAt != null && entry.createdAt.toLocalDate() >= it }
        } ?: { true }

        val entryTeamFilter: (SCMCatalogEntry) -> Boolean = { entry: SCMCatalogEntry ->
            when {
                filter.team.isNullOrBlank() -> true
                filter.team == "-" -> entry.teams.isNullOrEmpty()
                else -> entry.teams?.any {
                    it.id.contains(filter.team, ignoreCase = true) ||
                            (it.name?.contains(filter.team, ignoreCase = true) ?: false)
                } ?: false
            }
            // No filter
        }

        val entryFilter: (SCMCatalogEntry) -> Boolean = entryScmFilter and
                entryConfigFilter and
                entryRepositoryFilter and
                entryLinkFilter and
                entryBeforeLastActivityFilter and
                entryAfterLastActivityFilter and
                entryBeforeCreatedAtFilter and
                entryAfterCreatedAtFilter and
                entryTeamFilter

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
            securityService.asAdmin {
                structureService.projectList
            }.asSequence().filter(projectFilter).map { project ->
                SCMCatalogEntryOrProject.orphanProject(project)
            }
        }

        // ALL --> filtered entries + orphan projects
        // ENTRIES --> filtered entries
        // LINKED, UNLINKED --> filtered entries
        // ORPHAN --> orphan projects only

        val allEntries: Sequence<SCMCatalogEntryOrProject> = when (filter.link) {
            SCMCatalogProjectFilterLink.ALL -> entries() + orphanProjects()
            SCMCatalogProjectFilterLink.ENTRY -> entries()
            SCMCatalogProjectFilterLink.LINKED -> entries()
            SCMCatalogProjectFilterLink.UNLINKED -> entries()
            SCMCatalogProjectFilterLink.ORPHAN -> orphanProjects()
        }

        // Sorting
        val sortOn = filter.sortOn ?: SCMCatalogProjectFilterSort.REPOSITORY
        val comparator: Comparator<SCMCatalogEntryOrProject> =
            compareBy(sortOn.sortingSelector).run {
                if (filter.sortAscending) {
                    this
                } else {
                    reversed()
                }
            }

        // Sorting & truncating
        return allEntries.sortedWith(comparator).drop(filter.offset).take(filter.size).toList()
    }

    private fun getEntryLinkFilter(link: SCMCatalogProjectFilterLink): (SCMCatalogEntry) -> Boolean = { entry ->
        when (link) {
            SCMCatalogProjectFilterLink.ALL -> true
            SCMCatalogProjectFilterLink.ENTRY -> true
            SCMCatalogProjectFilterLink.LINKED -> catalogLinkService.isLinked(entry)
            SCMCatalogProjectFilterLink.UNLINKED -> !catalogLinkService.isLinked(entry)
            SCMCatalogProjectFilterLink.ORPHAN -> false
        }
    }

}
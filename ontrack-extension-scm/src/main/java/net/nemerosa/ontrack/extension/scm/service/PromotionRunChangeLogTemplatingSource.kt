package net.nemerosa.ontrack.extension.scm.service

import kotlinx.coroutines.runBlocking
import net.nemerosa.ontrack.extension.scm.changelog.DependencyLink
import net.nemerosa.ontrack.extension.scm.changelog.SCMChangeLog
import net.nemerosa.ontrack.extension.scm.changelog.SCMChangeLogService
import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.buildfilter.BuildFilterService
import net.nemerosa.ontrack.model.docs.Documentation
import net.nemerosa.ontrack.model.docs.DocumentationExampleCode
import net.nemerosa.ontrack.model.events.EventRenderer
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.model.templating.AbstractTemplatingSource
import net.nemerosa.ontrack.model.templating.getBooleanTemplatingParam
import net.nemerosa.ontrack.model.templating.getListStringsTemplatingParam
import org.springframework.stereotype.Component

@Component
@APIDescription(
    """
    Renders a change log for this promotion run.
    
    The "to build" is the one being promoted.
     
    The "from build" is the last build (before this one) having been promoted to the associated
    promotion level.
    
    If no such previous build is found on the associated branch, the search will be done
    across the whole project, unless the `acrossBranches` configuration parameter is set to `false`.
    
    If `project` is set to a comma-separated list of strings, the change log will be rendered 
    for the recursive links, in the order to the projects being set (going deeper and deeper
    in the links). 
"""
)
@Documentation(PromotionRunChangeLogTemplatingSourceConfig::class)
@DocumentationExampleCode("${'$'}{promotionRun.changelog}")
class PromotionRunChangeLogTemplatingSource(
    private val structureService: StructureService,
    private val buildFilterService: BuildFilterService,
    private val scmChangeLogService: SCMChangeLogService,
    private val entityDisplayNameService: EntityDisplayNameService,
) : AbstractTemplatingSource(
    field = "changelog",
    type = ProjectEntityType.PROMOTION_RUN,
) {

    override fun render(entity: ProjectEntity, configMap: Map<String, String>, renderer: EventRenderer): String {
        val empty = configMap["empty"] ?: ""
        return if (entity is PromotionRun) {
            val title =
                configMap.getBooleanTemplatingParam(PromotionRunChangeLogTemplatingSourceConfig::title.name, false)
            val acrossBranches = configMap.getBooleanTemplatingParam(
                PromotionRunChangeLogTemplatingSourceConfig::acrossBranches.name,
                true
            )
            val dependencies =
                configMap.getListStringsTemplatingParam(PromotionRunChangeLogTemplatingSourceConfig::dependencies.name)
                    ?: emptyList()
            val allQualifiers = configMap.getBooleanTemplatingParam(
                PromotionRunChangeLogTemplatingSourceConfig::allQualifiers.name,
                false
            )
            val defaultQualifierFallback = configMap.getBooleanTemplatingParam(
                PromotionRunChangeLogTemplatingSourceConfig::defaultQualifierFallback.name,
                false
            )

            // First boundary is the build being promoted
            val toBuild = entity.build
            // We now need to look for the previous promotion, on the same branch first
            var fromBuild = getPreviousPromotionOnBranch(entity)
            // If nothing on the branch, we may try at project level
            if (fromBuild == null && acrossBranches) {
                fromBuild = getPreviousPromotionOnProject(entity)
            }
            // If no previous build, we don't have any change log
            if (fromBuild == null) {
                empty
            }
            // We now have two boundaries
            else {
                if (allQualifiers && dependencies.isNotEmpty()) {
                    renderAllQualifiers(fromBuild, toBuild, dependencies, defaultQualifierFallback, renderer, empty, title)
                } else {
                    // Single change log
                    renderChangeLog(fromBuild, toBuild, dependencies, defaultQualifierFallback, renderer, empty, title)
                }
            }
        } else {
            empty
        }
    }

    private fun renderAllQualifiers(
        fromBuild: Build,
        toBuild: Build,
        dependencies: List<String>,
        defaultQualifierFallback: Boolean,
        renderer: EventRenderer,
        empty: String,
        title: Boolean,
    ): String {
        // Gets the change log boundaries but for the last dependency
        val parsedDependencies = dependencies.map { DependencyLink.parse(it) }
        val firstDependencies = parsedDependencies.dropLast(1)
        val lastDependencyProject = parsedDependencies.last().project
        val (deepFrom, deepTo) = runBlocking {
            scmChangeLogService.getChangeLogBoundaries(
                from = fromBuild,
                to = toBuild,
                dependencies = firstDependencies,
                defaultQualifierFallback = defaultQualifierFallback,
            )
        } ?: return ""

        // Gets all links to the last dependency project and all the common qualifiers
        // Including the default qualifier
        val qualifiers = collectAllQualifiers(lastDependencyProject, deepFrom, deepTo)

        // For each qualifier (including the default one), get a change log
        val qualifiedChangeLogs = runBlocking {
            qualifiers.associateWith { qualifier ->
                scmChangeLogService.getChangeLog(
                    from = deepFrom,
                    to = deepTo,
                    dependencies = listOf(
                        DependencyLink(
                            project = lastDependencyProject,
                            qualifier = qualifier,
                        )
                    ),
                    defaultQualifierFallback = defaultQualifierFallback,
                )
            }
        }

        // Renders as a list
        return qualifiedChangeLogs.entries
            .filter { (_, changeLog) -> changeLog != null && !changeLog.isEmpty() }
            .joinToString(
                separator = renderer.renderSpace()
            ) { (qualifier, changeLog) ->
                check(changeLog != null) {
                    "At this stage, the change log cannot be null"
                }
                renderChangeLog(
                    changeLog = changeLog,
                    renderer = renderer,
                    empty = empty,
                    title = title,
                    projectNameSuffix = if (qualifier.isBlank()) {
                        ""
                    } else {
                        " [$qualifier]"
                    }
                )
            }
    }

    private fun collectAllQualifiers(projectName: String, vararg builds: Build): List<String> {
        val qualifiers = mutableSetOf<String>()
        for (build in builds) {
            val linkQualifiers = structureService.getQualifiedBuildsUsedBy(build, size = 100) {
                it.build.project.name == projectName
            }.pageItems.map { it.qualifier }.filter { it.isNotBlank() }.toSet()
            qualifiers += linkQualifiers
        }
        // Sorting the qualifiers
        val sortedQualifiers = qualifiers.sorted().toMutableList()
        // Always adding the default qualifier
        sortedQualifiers.add(0, "")
        // OK
        return sortedQualifiers.toList()
    }

    private fun renderChangeLog(
        fromBuild: Build,
        toBuild: Build,
        dependencies: List<String>,
        defaultQualifierFallback: Boolean,
        renderer: EventRenderer,
        empty: String,
        title: Boolean
    ): String {
        // ... getting the change log, recursively or not
        val changeLog = runBlocking {
            scmChangeLogService.getChangeLog(
                fromBuild,
                toBuild,
                dependencies.map { DependencyLink.parse(it) },
                defaultQualifierFallback,
            )
        }
        return renderChangeLog(changeLog, renderer, empty, title)
    }

    private fun renderChangeLog(
        changeLog: SCMChangeLog?,
        renderer: EventRenderer,
        empty: String,
        title: Boolean,
        projectNameSuffix: String = "",
    ): String {
        // Rendered change log
        val renderedChangeLog = changeLog
            ?.takeIf { it.from.id() != it.to.id() }
            ?.let { scmChangeLog ->
                renderChangeLog(
                    changeLog = scmChangeLog,
                    renderer = renderer
                )
            } ?: empty
        // Title?
        return if (title) {
            if (changeLog != null) {

                val projectName = entityDisplayNameService.render(changeLog.from.project, renderer)
                val fromName = entityDisplayNameService.render(changeLog.from, renderer)
                val toName = entityDisplayNameService.render(changeLog.to, renderer)

                val titleText = if (changeLog.from.id() != changeLog.to.id()) {
                    """
                            Change log for $projectName$projectNameSuffix from $fromName to $toName
                        """.trimIndent()

                } else {
                    """
                            Project $projectName$projectNameSuffix version $fromName
                        """.trimIndent()
                }

                renderer.renderSection(
                    title = titleText,
                    content = renderedChangeLog,
                )
            } else {
                // Change log not defined, and still asking for a title without a reference
                // Skipping
                ""
            }
        } else {
            renderedChangeLog
        }
    }

    private fun renderChangeLog(changeLog: SCMChangeLog, renderer: EventRenderer): String {
        return renderer.renderList(
            changeLog.issues?.issues?.map { issue ->
                val link = renderer.renderLink(
                    text = issue.displayKey,
                    href = issue.url,
                )
                val text = issue.summary
                "$link $text"
            } ?: emptyList()
        )
    }

    private fun getPreviousPromotionOnBranch(run: PromotionRun): Build? {
        val builds = buildFilterService.standardFilterProviderData(2) // We need the current build, and the previous one
            .withWithPromotionLevel(run.promotionLevel.name)
            .build()
            .filterBranchBuilds(run.promotionLevel.branch)
        return builds.drop(1).firstOrNull()
    }

    private fun getPreviousPromotionOnProject(run: PromotionRun): Build? {
        val builds = structureService.buildSearch(
            projectId = run.project.id,
            form = BuildSearchForm(
                maximumCount = 2,  // We need the current build, and the previous one
                promotionName = run.promotionLevel.name,
            )
        )
        return builds.drop(1).firstOrNull()
    }

}
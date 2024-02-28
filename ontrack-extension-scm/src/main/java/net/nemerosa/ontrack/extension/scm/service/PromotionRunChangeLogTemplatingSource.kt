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
                // ... getting the change log, recursively or not
                val changeLog = runBlocking {
                    scmChangeLogService.getChangeLog(
                        fromBuild,
                        toBuild,
                        dependencies.map { DependencyLink.parse(it) },
                    )
                }
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
                if (title) {
                    if (changeLog != null) {

                        val projectName = entityDisplayNameService.render(changeLog.from.project, renderer)
                        val fromName = entityDisplayNameService.render(changeLog.from, renderer)
                        val toName = entityDisplayNameService.render(changeLog.to, renderer)

                        val titleText = if (changeLog.from.id() != changeLog.to.id()) {
                            """
                            Change log for $projectName from $fromName to $toName
                        """.trimIndent()

                        } else {
                            """
                            Project $projectName version $fromName
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
        } else {
            empty
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
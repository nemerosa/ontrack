package net.nemerosa.ontrack.extension.scm.service

import kotlinx.coroutines.runBlocking
import net.nemerosa.ontrack.extension.scm.changelog.SCMChangeLog
import net.nemerosa.ontrack.extension.scm.changelog.SCMChangeLogService
import net.nemerosa.ontrack.model.buildfilter.BuildFilterService
import net.nemerosa.ontrack.model.events.EventRenderer
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.model.templating.AbstractTemplatingSource
import net.nemerosa.ontrack.model.templating.getBooleanTemplatingParam
import org.springframework.stereotype.Component

@Component
class PromotionRunChangeLogTemplatingSource(
    private val buildFilterService: BuildFilterService,
    private val scmChangeLogService: SCMChangeLogService,
) : AbstractTemplatingSource(
    field = "changelog",
    type = ProjectEntityType.PROMOTION_RUN,
) {

    override fun render(entity: ProjectEntity, configMap: Map<String, String>, renderer: EventRenderer): String =
        if (entity is PromotionRun) {
            val useProject = configMap.getBooleanTemplatingParam("useProject", true)
            // First boundary is the build being promoted
            val toBuild = entity.build
            // We now need to look for the previous promotion, on the same branch first
            var fromBuild = getPreviousPromotionOnBranch(entity)
            // If nothing on the branch, we may try at project level
            if (fromBuild == null && useProject) {
                fromBuild = getPreviousPromotionOnProject(entity)
            }
            // If no previous build, we don't have any change log
            if (fromBuild == null) {
                ""
            }
            // We now have two boundaries
            else {
                // Getting the change log
                val changeLog = runBlocking {
                    scmChangeLogService.getChangeLog(
                        from = fromBuild,
                        to = toBuild
                    )
                }
                // Rendering it
                renderChangeLog(changeLog, renderer)
            }
        } else {
            ""
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
        TODO()
    }

}
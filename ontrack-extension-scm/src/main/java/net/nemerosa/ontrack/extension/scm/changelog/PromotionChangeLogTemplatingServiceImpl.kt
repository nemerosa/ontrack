package net.nemerosa.ontrack.extension.scm.changelog

import net.nemerosa.ontrack.extension.scm.service.PromotionRunChangeLogTemplatingSourceConfig
import net.nemerosa.ontrack.model.buildfilter.BuildFilterService
import net.nemerosa.ontrack.model.events.EventRenderer
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.model.templating.getBooleanTemplatingParam
import net.nemerosa.ontrack.model.templating.getListStringsTemplatingParam
import org.springframework.stereotype.Service

@Service
class PromotionChangeLogTemplatingServiceImpl(
    private val buildFilterService: BuildFilterService,
    private val structureService: StructureService,
    private val changeLogTemplatingService: ChangeLogTemplatingService,
) : PromotionChangeLogTemplatingService {

    override fun render(
        toBuild: Build,
        promotion: String,
        configMap: Map<String, String>,
        renderer: EventRenderer
    ): String {

        val acrossBranches = configMap.getBooleanTemplatingParam(
            PromotionRunChangeLogTemplatingSourceConfig::acrossBranches.name,
            true
        )

        // We now need to look for the previous promotion, on the same branch first
        var fromBuild = getPreviousPromotionOnBranch(toBuild.branch, promotion)
        // If nothing on the branch, we may try at project level
        if (fromBuild == null && acrossBranches) {
            fromBuild = getPreviousPromotionOnProject(toBuild.project, promotion)
        }
        // If no previous build, we don't have any change log
        return if (fromBuild == null) {
            ChangeLogTemplatingServiceConfig.emptyValue(configMap)
        }
        // We now have two boundaries
        else {

            changeLogTemplatingService.render(
                fromBuild = fromBuild,
                toBuild = toBuild,
                configMap = configMap,
                renderer = renderer
            )
        }
    }

    private fun getPreviousPromotionOnBranch(branch: Branch, promotion: String): Build? {
        val builds = buildFilterService.standardFilterProviderData(2)
            // We need the current build, and the previous one
            .withWithPromotionLevel(promotion)
            .build()
            .filterBranchBuilds(branch)
        return builds.drop(1).firstOrNull()
    }

    private fun getPreviousPromotionOnProject(project: Project, promotion: String): Build? {
        val builds = structureService.buildSearch(
            projectId = project.id,
            form = BuildSearchForm(
                maximumCount = 2,  // We need the current build, and the previous one
                promotionName = promotion,
            )
        )
        return builds.drop(1).firstOrNull()
    }

}
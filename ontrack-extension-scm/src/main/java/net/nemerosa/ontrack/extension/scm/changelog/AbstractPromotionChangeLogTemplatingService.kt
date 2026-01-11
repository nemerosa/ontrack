package net.nemerosa.ontrack.extension.scm.changelog

import net.nemerosa.ontrack.model.buildfilter.BuildFilterService
import net.nemerosa.ontrack.model.structure.*

abstract class AbstractPromotionChangeLogTemplatingService(
    private val structureService: StructureService,
    private val buildFilterService: BuildFilterService,
) {

    fun promotionBoundaries(
        toBuild: Build,
        promotion: String,
        acrossBranches: Boolean,
    ): Build? {

        // We now need to look for the previous promotion, on the same branch first
        var fromBuild = getPreviousPromotionOnBranch(toBuild.branch, promotion)
        // If nothing on the branch, we may try at the project level
        if (fromBuild == null && acrossBranches) {
            fromBuild = getPreviousPromotionOnProject(toBuild.project, promotion)
        }
        // If no previous build, we don't have any change log
        return if (fromBuild == null) {
            null
        }
        // We now have two boundaries
        else {
            fromBuild
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
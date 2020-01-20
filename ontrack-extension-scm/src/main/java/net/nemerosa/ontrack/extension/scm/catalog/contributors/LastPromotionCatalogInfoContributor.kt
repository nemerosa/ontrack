package net.nemerosa.ontrack.extension.scm.catalog.contributors

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.scm.SCMExtensionFeature
import net.nemerosa.ontrack.extension.scm.catalog.SCMCatalogEntry
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.model.buildfilter.BuildFilterService
import net.nemerosa.ontrack.model.exceptions.PromotionRunNotFoundException
import net.nemerosa.ontrack.model.ordering.BranchOrderingService
import net.nemerosa.ontrack.model.structure.*
import org.springframework.stereotype.Component

@Component
class LastPromotionCatalogInfoContributor(
        private val branchModelMatcherService: BranchModelMatcherService,
        private val structureService: StructureService,
        private val buildFilterService: BuildFilterService,
        brandOrderingService: BranchOrderingService,
        extension: SCMExtensionFeature
) : AbstractCoreCatalogInfoContributor<LastPromotionCatalogInfo>(extension) {

    override val name: String = "Last promotion"

    /**
     * Ordering based on version
     */
    private val ordering = brandOrderingService.getBranchOrdering("version")
            ?: throw IllegalStateException("Cannot find `version`-based ordering.")

    override fun collectInfo(project: Project, entry: SCMCatalogEntry): LastPromotionCatalogInfo? {
        // Gets the branch model
        return branchModelMatcherService.getBranchModelMatcher(project)?.let { _ ->
            // Version-based ordering
            val versionComparator = ordering.getComparator(".*")
            // Gets the list of branches for the source project
            // ... order them by version, get the last one
            val latestBranch = structureService.getBranchesForProject(project.id).minWith(versionComparator)
            // Gets the last promotion for this branch
            val lastPromotion = latestBranch?.let { structureService.getPromotionLevelListForBranch(latestBranch.id).lastOrNull() }
            // Gets the last build having this promotion for this branch
            val latestPromotedBuild = lastPromotion?.let {
                buildFilterService.standardFilterProviderData(1)
                        .withWithPromotionLevel(lastPromotion.name)
                        .build()
                        .filterBranchBuilds(lastPromotion.branch)
                        .firstOrNull()
            }
            // Gets the last build promotion run
            val latestPromotionRun = if (lastPromotion != null && latestPromotedBuild != null) {
                structureService.getPromotionRunsForBuildAndPromotionLevel(latestPromotedBuild, lastPromotion).lastOrNull()
            } else {
                null
            }
            // OK
            latestPromotionRun?.run { LastPromotionCatalogInfo(this) }
        }
    }

    override fun asStoredJson(info: LastPromotionCatalogInfo): JsonNode =
            InternalInfo(info.promotionRun.id()).asJson()

    override fun fromStoredJson(node: JsonNode): LastPromotionCatalogInfo? =
            node.parse<InternalInfo>().run {
                try {
                    structureService.getPromotionRun(ID.of(promotionRunId))
                } catch (_: PromotionRunNotFoundException) {
                    null
                }
            }?.run {
                LastPromotionCatalogInfo(this)
            }

    override fun asClientJson(info: LastPromotionCatalogInfo): JsonNode = info.asJson()

    private data class InternalInfo(val promotionRunId: Int)


}

data class LastPromotionCatalogInfo(
        val promotionRun: PromotionRun
)
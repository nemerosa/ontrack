package net.nemerosa.ontrack.service.promotions

import net.nemerosa.ontrack.extension.api.BuildPromotionInfoExtension
import net.nemerosa.ontrack.extension.api.ExtensionManager
import net.nemerosa.ontrack.model.promotions.BuildPromotionInfo
import net.nemerosa.ontrack.model.promotions.BuildPromotionInfoItem
import net.nemerosa.ontrack.model.promotions.BuildPromotionInfoService
import net.nemerosa.ontrack.model.promotions.LinkedBuildPromotionInfoItems
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.StructureService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class BuildPromotionInfoServiceImpl(
    private val extensionManager: ExtensionManager,
    private val structureService: StructureService,
) : BuildPromotionInfoService {

    private val extensions: Collection<BuildPromotionInfoExtension> by lazy {
        extensionManager.getExtensions(BuildPromotionInfoExtension::class.java)
    }

    override fun getBuildPromotionInfo(build: Build): BuildPromotionInfo {
        // Getting the list of promotion levels
        val promotionLevels = structureService.getPromotionLevelListForBranch(build.branch.id)
            // Getting them from the highest to the lowest
            .reversed()
        // For each promotion level, getting the information
        val withPromotionItems = promotionLevels.map { promotionLevel ->
            // List of items to collect
            val items = mutableListOf<BuildPromotionInfoItem<*>>()
            // TODO Extensions (after promotion level)
            // Promotion level itself
            items += buildPromotionInfoItemForPromotionLevel(promotionLevel)
            // Promotion runs
            items += structureService.getPromotionRunsForBuildAndPromotionLevel(build, promotionLevel).map {
                buildPromotionInfoItemForPromotionRun(it)
            }
            // TODO Extensions (before promotion level)
            // OK
            LinkedBuildPromotionInfoItems(
                promotionLevel = promotionLevel,
                items = items,
            )
        }
        // TODO No promotion items (extensions only)
        // OK
        return BuildPromotionInfo(
            noPromotionItems = emptyList(),
            withPromotionItems = withPromotionItems,
        )
    }

}
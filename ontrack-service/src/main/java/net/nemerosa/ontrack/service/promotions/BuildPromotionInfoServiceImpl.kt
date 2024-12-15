package net.nemerosa.ontrack.service.promotions

import net.nemerosa.ontrack.extension.api.BuildPromotionInfoExtension
import net.nemerosa.ontrack.extension.api.ExtensionManager
import net.nemerosa.ontrack.model.promotions.BuildPromotionInfo
import net.nemerosa.ontrack.model.promotions.BuildPromotionInfoItem
import net.nemerosa.ontrack.model.promotions.BuildPromotionInfoService
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
        // Core promotion info items (promotion levels & promotion runs
        val items = mutableListOf<BuildPromotionInfoItem<*>>()
        // Getting the list of promotion levels
        val promotionLevels = structureService.getPromotionLevelListForBranch(build.branch.id)
            // Getting them from the highest to the lowest
            .reversed()
        // For each promotion level
        promotionLevels.forEach { promotionLevel ->
            val runs = structureService.getPromotionRunsForBuildAndPromotionLevel(build, promotionLevel)
            // Promotion level itself only if no run
            if (runs.isEmpty()) {
                items += buildPromotionInfoItemForPromotionLevel(promotionLevel)
            }
            // Promotion runs for this promotion level
            items += runs.map {
                buildPromotionInfoItemForPromotionRun(it)
            }
        }
        // Extensions
        extensions.forEach { extension ->
            extension.buildPromotionInfoItems(items, build, promotionLevels)
        }
        // OK
        return BuildPromotionInfo(
            items = items,
        )
    }

}
package net.nemerosa.ontrack.extension.general

import net.nemerosa.ontrack.extension.api.PromotionRunCheckExtension
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.structure.*
import org.springframework.stereotype.Component

/**
 * [PromotionRunCheckExtension] based on the [PreviousPromotionConditionPropertyType] property value.
 *
 * The cascade itself lives in [PreviousPromotionConditionService], which the delivery map also reads:
 * what this check refuses and what the map draws are then the same answer by construction.
 */
@Component
class PreviousPromotionConditionCheckExtension(
        private val structureService: StructureService,
        extensionFeature: GeneralExtensionFeature,
        private val previousPromotionConditionService: PreviousPromotionConditionService,
) : AbstractExtension(extensionFeature), PromotionRunCheckExtension {

    override fun checkPromotionRunCreation(promotionRun: PromotionRun) {
        // Promotion to grant
        val promotion = promotionRun.promotionLevel
        // List of all promotions for the branch
        val promotions = structureService.getPromotionLevelListForBranch(promotion.branch.id)
        // Index of the promotion to grant
        val index = promotions.indexOfFirst { it.id() == promotion.id() }
        // There is a previous promotion
        if (index > 0) {
            val previousPromotion = promotions[index - 1]
            // Checks if the build is granted this promotion
            val build = promotionRun.build
            val previousPromotions = structureService.getPromotionRunsForBuildAndPromotionLevel(build, previousPromotion)
            val previousPromotionGranted = previousPromotions.isNotEmpty()
            // If previous promotion NOT granted, we have to check the configuration
            // If not, this does not matter
            if (!previousPromotionGranted) {
                val resolution = previousPromotionConditionService.resolvePreviousPromotionCondition(promotion)
                if (resolution.required) {
                    // Which exception says WHERE the refusal comes from, which is the question the
                    // user actually asks - the delivery map deliberately does not answer it
                    val source = resolution.source
                    throw if (source != null) {
                        PreviousPromotionRequiredException(previousPromotion, promotion, source)
                    } else {
                        PreviousPromotionRequiredGlobalException(previousPromotion, promotion)
                    }
                }
            }
        }
    }

    override val order: Int = 0
}

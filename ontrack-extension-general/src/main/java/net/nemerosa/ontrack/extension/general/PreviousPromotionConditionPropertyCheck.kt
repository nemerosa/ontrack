package net.nemerosa.ontrack.extension.general

import net.nemerosa.ontrack.model.structure.*
import org.springframework.stereotype.Component

/**
 * [PromotionRunCheck] based on the [PreviousPromotionConditionPropertyType] property value.
 */
@Component
class PreviousPromotionConditionPropertyCheck(
        private val structureService: StructureService,
        private val propertyService: PropertyService
) : PromotionRunCheck {

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
            // If previous promotion NOT granted, we have to check the properties
            // If not, this does not matter
            if (!previousPromotionGranted) {
                checkPreviousPromotionConditionProperty(previousPromotion, promotion, promotionRun.promotionLevel)
                checkPreviousPromotionConditionProperty(previousPromotion, promotion, promotionRun.promotionLevel.branch)
                checkPreviousPromotionConditionProperty(previousPromotion, promotion, promotionRun.promotionLevel.branch.project)
            }
        }
    }

    private fun checkPreviousPromotionConditionProperty(
            previousPromotion: PromotionLevel,
            promotion: PromotionLevel,
            entity: ProjectEntity
    ) {
        val property: PreviousPromotionConditionProperty? = propertyService.getProperty(entity, PreviousPromotionConditionPropertyType::class.java).value
        if (property != null && property.previousPromotionRequired) {
            throw PreviousPromotionRequiredException(
                    previousPromotion,
                    promotion,
                    entity
            )
        }
    }

    override val order: Int = 0
}
package net.nemerosa.ontrack.extension.general

import net.nemerosa.ontrack.extension.api.PromotionRunCheckExtension
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.structure.*
import org.springframework.stereotype.Component

/**
 * [PromotionRunCheckExtension] based on the [PreviousPromotionConditionPropertyType] property value.
 */
@Component
class PreviousPromotionConditionCheckExtension(
        private val structureService: StructureService,
        private val propertyService: PropertyService,
        extensionFeature: GeneralExtensionFeature
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
            // If previous promotion NOT granted, we have to check the properties
            // If not, this does not matter
            if (!previousPromotionGranted) {
                // Promotion level first...
                checkPreviousPromotionConditionProperty(previousPromotion, promotion, promotionRun.promotionLevel)
                        // ... then branch
                        && checkPreviousPromotionConditionProperty(previousPromotion, promotion, promotionRun.promotionLevel.branch)
                        // ... then project
                        && checkPreviousPromotionConditionProperty(previousPromotion, promotion, promotionRun.promotionLevel.branch.project)
            }
        }
    }

    /**
     * Returns `false` if the condition has been checked explicitly OK and that there is no need to check further.
     */
    private fun checkPreviousPromotionConditionProperty(
            previousPromotion: PromotionLevel,
            promotion: PromotionLevel,
            entity: ProjectEntity
    ): Boolean {
        val property: PreviousPromotionConditionProperty? = propertyService.getProperty(entity, PreviousPromotionConditionPropertyType::class.java).value
        return if (property != null) {
            if (property.previousPromotionRequired) {
                throw PreviousPromotionRequiredException(
                        previousPromotion,
                        promotion,
                        entity
                )
            } else {
                false // No need to check further
            }
        } else {
            true // Could not check at this level, need to go on
        }
    }

    override val order: Int = 0
}
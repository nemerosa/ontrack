package net.nemerosa.ontrack.model.promotions

import net.nemerosa.ontrack.model.structure.PromotionLevel

/**
 * Information about a build's promotion.
 *
 * This can be used to hold any type of information
 * linked to the promotion of a build, like a promotion
 * level only, a promotion runs, deployable environments, etc.
 *
 * @param T Type of information contained
 * @property promotionLevel Linked promotion level if any
 * @property data Information data
 */
data class BuildPromotionInfoItem<T : Any>(
    val promotionLevel: PromotionLevel?,
    val data: T,
)

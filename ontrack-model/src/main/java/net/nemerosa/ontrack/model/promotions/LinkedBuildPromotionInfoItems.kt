package net.nemerosa.ontrack.model.promotions

import net.nemerosa.ontrack.model.structure.PromotionLevel

/**
 * [BuildPromotionInfoItem] list linked to promotion level
 *
 * @property promotionLevel Linked promotion level
 * @property items All information items linked to this promotion level
 */
data class LinkedBuildPromotionInfoItems(
    val promotionLevel: PromotionLevel,
    val items: List<BuildPromotionInfoItem<*>>,
)

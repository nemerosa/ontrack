package net.nemerosa.ontrack.model.promotions

/**
 * The whole information about the promotions of a build.
 *
 * @property noPromotionItems All items which could not be linked to a promotion
 * @property withPromotionItems All items which can be grouped per promotion
 */
data class BuildPromotionInfo(
    val noPromotionItems: List<BuildPromotionInfoItem<*>>,
    val withPromotionItems: List<LinkedBuildPromotionInfoItems>,
)

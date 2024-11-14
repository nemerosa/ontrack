package net.nemerosa.ontrack.model.promotions

/**
 * The whole information about the promotions of a build.
 *
 * @property items All items which could not be linked to a promotion (or not)
 */
data class BuildPromotionInfo(
    val items: List<BuildPromotionInfoItem<*>>,
)

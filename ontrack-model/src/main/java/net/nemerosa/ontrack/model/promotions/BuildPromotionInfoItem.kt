package net.nemerosa.ontrack.model.promotions

/**
 * Information about a build's promotion.
 *
 * This can be used to hold any type of information
 * linked to the promotion of a build, like a promotion
 * level only, a list of promotion runs, deployable environments, etc.
 *
 * @param T Type of information contained
 * @property data Information data
 */
data class BuildPromotionInfoItem<T : Any>(
    val data: T,
)

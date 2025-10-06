package net.nemerosa.ontrack.model.structure

/**
 * Configuration for a promotion level.
 */
interface PromotionLevelConfigurator {

    fun configure(pl: PromotionLevel, config: PromotionLevelConfiguration)

}
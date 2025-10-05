package net.nemerosa.ontrack.extension.config.model

import net.nemerosa.ontrack.model.structure.PromotionLevel

/**
 * Configuration for a promotion level.
 */
interface PromotionLevelConfigurator {

    fun configure(pl: PromotionLevel, config: PromotionLevelConfiguration)

}
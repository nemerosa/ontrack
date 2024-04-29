package net.nemerosa.ontrack.extension.av.model

import net.nemerosa.ontrack.extension.av.config.AutoVersioningConfiguredBranch
import net.nemerosa.ontrack.model.structure.PromotionRun

/**
 * List of configured branches for an auto versioning event.
 */
data class AutoVersioningConfiguredBranches(
    val configuredBranches: List<AutoVersioningConfiguredBranch>,
    val promotionRun: PromotionRun,
)
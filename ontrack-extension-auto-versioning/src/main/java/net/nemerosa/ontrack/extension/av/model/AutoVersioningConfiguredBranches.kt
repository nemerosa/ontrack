package net.nemerosa.ontrack.extension.av.model

import net.nemerosa.ontrack.extension.av.config.AutoVersioningConfiguredBranch

/**
 * List of configured branches for an auto versioning event.
 */
data class AutoVersioningConfiguredBranches(
    val configuredBranches: List<AutoVersioningConfiguredBranch>,
    val promotionEvent: PromotionEvent,
)
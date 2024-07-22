package net.nemerosa.ontrack.extension.av.tracking

import net.nemerosa.ontrack.extension.av.config.AutoVersioningConfiguredBranch

interface AutoVersioningTracking {

    /**
     * Initializing the trail
     */
    fun init(
        configuredBranches: List<AutoVersioningConfiguredBranch>
    ): List<AutoVersioningBranchTrail>

    /**
     * Given an existing trail, modifies it and registers its new state.
     */
    fun withTrail(code: (trail: AutoVersioningTrail) -> AutoVersioningTrail): AutoVersioningTrail

    /**
     * Gets the current trail
     */
    val trail: AutoVersioningTrail?

}
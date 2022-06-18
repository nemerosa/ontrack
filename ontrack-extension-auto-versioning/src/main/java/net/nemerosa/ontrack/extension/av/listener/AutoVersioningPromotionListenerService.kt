package net.nemerosa.ontrack.extension.av.listener

import net.nemerosa.ontrack.extension.av.model.AutoVersioningConfiguredBranches
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.PromotionLevel

/**
 * Service used to select & configure branches eligible for auto versioning based on an event
 */
interface AutoVersioningPromotionListenerService {
    /**
     * Given a [build] promoted to a given [promotion], returns a list of configured branches eligible for auto versioning.
     */
    fun getConfiguredBranches(build: Build, promotion: PromotionLevel): AutoVersioningConfiguredBranches
}
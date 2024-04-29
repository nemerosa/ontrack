package net.nemerosa.ontrack.extension.av.listener

import net.nemerosa.ontrack.extension.av.model.AutoVersioningConfiguredBranches
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.PromotionLevel
import net.nemerosa.ontrack.model.structure.PromotionRun

/**
 * Service used to select & configure branches eligible for auto versioning based on an event
 */
interface AutoVersioningPromotionListenerService {
    /**
     * Given a promotion run, returns a list of configured branches eligible for auto versioning.
     *
     * @return Null if no auto versioning can be performed
     */
    fun getConfiguredBranches(promotionRun: PromotionRun): AutoVersioningConfiguredBranches?
}
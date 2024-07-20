package net.nemerosa.ontrack.extension.av.listener

import net.nemerosa.ontrack.extension.av.config.AutoVersioningConfiguredBranch
import net.nemerosa.ontrack.extension.av.model.AutoVersioningConfiguredBranches
import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.PromotionLevel
import net.nemerosa.ontrack.model.structure.PromotionRun

/**
 * Service used to select & configure branches eligible for auto versioning based on an event
 */
interface AutoVersioningPromotionListenerService {

    /**
     * Given a promotion run, returns a list of configured branches eligible for auto versioning.
     */
    fun getConfiguredBranches(promotionRun: PromotionRun): AutoVersioningConfiguredBranches =
        AutoVersioningConfiguredBranches(
            configuredBranches = getConfiguredBranches(promotionRun.promotionLevel),
            promotionRun = promotionRun,
        )

    /**
     * Given a promotion level, returns a list of configured branches eligible for auto versioning.
     */
    fun getConfiguredBranches(promotionLevel: PromotionLevel): List<AutoVersioningConfiguredBranch>

    /**
     * Checks if a branch auto-versioning is valid according to its project's rules
     */
    fun acceptBranchWithProjectAVRules(branch: Branch): Boolean
}
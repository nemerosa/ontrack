package net.nemerosa.ontrack.extension.av.listener

import net.nemerosa.ontrack.extension.av.config.AutoVersioningConfiguredBranch
import net.nemerosa.ontrack.extension.av.model.AutoVersioningConfiguredBranches
import net.nemerosa.ontrack.extension.av.tracking.AutoVersioningBranchTrail
import net.nemerosa.ontrack.extension.av.tracking.AutoVersioningTracking
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
    fun getConfiguredBranches(
        promotionRun: PromotionRun,
        tracking: AutoVersioningTracking,
    ): List<AutoVersioningBranchTrail> =
        getConfiguredBranches(
            promotionLevel = promotionRun.promotionLevel,
            tracking = tracking,
        )

    /**
     * Given a promotion level, returns a list of configured branches eligible for auto versioning.
     */
    fun getConfiguredBranches(
        promotionLevel: PromotionLevel,
        tracking: AutoVersioningTracking,
    ): List<AutoVersioningBranchTrail>

    /**
     * Checks if a branch auto-versioning is valid according to its project's rules
     */
    fun acceptBranchWithProjectAVRules(branch: Branch): Boolean
}
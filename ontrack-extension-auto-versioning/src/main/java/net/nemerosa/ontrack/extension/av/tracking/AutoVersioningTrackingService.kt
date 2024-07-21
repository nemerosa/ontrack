package net.nemerosa.ontrack.extension.av.tracking

import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.PromotionRun

interface AutoVersioningTrackingService {

    /**
     * Starts a trail for this promotion run.
     */
    fun start(run: PromotionRun): AutoVersioningTracking

    /**
     * Starts a trail to be stored in memory
     */
    fun startInMemoryTrail(): AutoVersioningTracking

    /**
     * Progressing the trail
     */
    fun withTrail(
        run: PromotionRun,
        code: (trail: AutoVersioningTrail) -> AutoVersioningTrail
    )

    /**
     * Gets the trail for a promotion run
     */
    fun getTrail(run: PromotionRun): AutoVersioningTrail?

}
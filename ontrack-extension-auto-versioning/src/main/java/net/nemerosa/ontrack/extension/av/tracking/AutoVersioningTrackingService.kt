package net.nemerosa.ontrack.extension.av.tracking

import net.nemerosa.ontrack.model.pagination.PaginatedList
import net.nemerosa.ontrack.model.structure.PromotionLevel
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
    ): AutoVersioningTrail

    /**
     * Gets the trail for a promotion run
     */
    fun getTrail(run: PromotionRun): AutoVersioningTrail?

    /**
     * Gets a paginated trail for a promotion run
     */
    fun getPaginatedTrail(
        run: PromotionRun,
        filter: AutoVersioningTrailFilter = AutoVersioningTrailFilter(),
        offset: Int,
        size: Int,
    ): PaginatedList<AutoVersioningBranchTrail>

    /**
     * Gets a paginated trail for a promotion level
     */
    fun getPromotionPaginatedTrail(
        promotionLevel: PromotionLevel,
        filter: AutoVersioningTrailFilter,
        offset: Int,
        size: Int
    ): PaginatedList<AutoVersioningBranchTrail>

}
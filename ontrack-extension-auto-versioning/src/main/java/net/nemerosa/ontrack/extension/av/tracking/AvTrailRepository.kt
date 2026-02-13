package net.nemerosa.ontrack.extension.av.tracking

import net.nemerosa.ontrack.model.structure.PromotionRun

interface AvTrailRepository {

    fun findByPromotionRun(run: PromotionRun, filter: AutoVersioningTrailFilter): StoredAutoVersioningTrail?

    fun findBranchesByPromotionRun(
        run: PromotionRun,
        filter: AutoVersioningTrailFilter,
        offset: Int,
        size: Int
    ): List<StoredBranchTrail>

    fun countBranchesByPromotionRun(run: PromotionRun, filter: AutoVersioningTrailFilter): Int

    fun saveForPromotionRun(run: PromotionRun, trail: StoredAutoVersioningTrail)

}
package net.nemerosa.ontrack.extension.av.tracking

import net.nemerosa.ontrack.common.RunProfile
import net.nemerosa.ontrack.extension.av.listener.AutoVersioningPromotionListenerService
import net.nemerosa.ontrack.model.pagination.PaginatedList
import net.nemerosa.ontrack.model.structure.PromotionLevel
import net.nemerosa.ontrack.model.structure.PromotionRun
import net.nemerosa.ontrack.model.structure.StructureService
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Service
@Profile(RunProfile.PROD)
@Transactional(propagation = Propagation.REQUIRES_NEW)
class AutoVersioningTrackingServiceImpl(
    avTrailRepository: AvTrailRepository,
    structureService: StructureService,
    autoVersioningPromotionListenerService: AutoVersioningPromotionListenerService,
) : AbstractAutoVersioningTrackingService(
    avTrailRepository = avTrailRepository,
    structureService = structureService,
    autoVersioningPromotionListenerService = autoVersioningPromotionListenerService
) {

    @Suppress("RedundantOverride")
    override fun start(run: PromotionRun): AutoVersioningTracking {
        return super.start(run)
    }

    @Suppress("RedundantOverride")
    override fun startInMemoryTrail(): AutoVersioningTracking {
        return super.startInMemoryTrail()
    }

    @Suppress("RedundantOverride")
    override fun withTrail(
        run: PromotionRun,
        code: (trail: AutoVersioningTrail) -> AutoVersioningTrail
    ): AutoVersioningTrail =
        super.withTrail(run, code)

    @Suppress("RedundantOverride")
    override fun getTrail(run: PromotionRun): AutoVersioningTrail? {
        return super.getTrail(run)
    }


    @Suppress("RedundantOverride")
    override fun getPaginatedTrail(
        run: PromotionRun,
        filter: AutoVersioningTrailFilter,
        offset: Int,
        size: Int
    ): PaginatedList<AutoVersioningBranchTrail> {
        return super.getPaginatedTrail(run, filter, offset, size)
    }

    @Suppress("RedundantOverride")
    override fun getPromotionPaginatedTrail(
        promotionLevel: PromotionLevel,
        filter: AutoVersioningTrailFilter,
        offset: Int,
        size: Int
    ): PaginatedList<AutoVersioningBranchTrail> {
        return super.getPromotionPaginatedTrail(promotionLevel, filter, offset, size)
    }
}
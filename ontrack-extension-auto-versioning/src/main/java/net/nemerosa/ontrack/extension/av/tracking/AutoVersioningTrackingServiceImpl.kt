package net.nemerosa.ontrack.extension.av.tracking

import net.nemerosa.ontrack.common.RunProfile
import net.nemerosa.ontrack.model.structure.EntityStore
import net.nemerosa.ontrack.model.structure.PromotionRun
import net.nemerosa.ontrack.model.structure.StructureService
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Service
@Profile("!${RunProfile.UNIT_TEST}")
@Transactional(propagation = Propagation.REQUIRES_NEW)
class AutoVersioningTrackingServiceImpl(
    entityStore: EntityStore,
    structureService: StructureService,
) : AbstractAutoVersioningTrackingService(entityStore, structureService) {

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
}
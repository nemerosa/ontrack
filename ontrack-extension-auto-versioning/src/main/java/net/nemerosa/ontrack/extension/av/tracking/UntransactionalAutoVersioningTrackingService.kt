package net.nemerosa.ontrack.extension.av.tracking

import jakarta.annotation.PostConstruct
import net.nemerosa.ontrack.common.RunProfile
import net.nemerosa.ontrack.extension.av.listener.AutoVersioningPromotionListenerService
import net.nemerosa.ontrack.model.structure.StructureService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Service
@Profile(RunProfile.DEV)
@Transactional(propagation = Propagation.REQUIRED)
class UntransactionalAutoVersioningTrackingService(
    avTrailRepository: AvTrailRepository,
    structureService: StructureService,
    autoVersioningPromotionListenerService: AutoVersioningPromotionListenerService,
) : AbstractAutoVersioningTrackingService(
    avTrailRepository = avTrailRepository,
    structureService = structureService,
    autoVersioningPromotionListenerService = autoVersioningPromotionListenerService
) {

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    @PostConstruct
    fun logging() {
        logger.warn("[auto-versioning] Using test auto versioning tracking service")
    }

}
package net.nemerosa.ontrack.extension.github.ingestion.payload

import net.nemerosa.ontrack.extension.github.ingestion.processing.IngestionEventProcessingResult
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.support.StorageService
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional
class DefaultIngestionHookPayloadStorage(
    storageService: StorageService,
    securityService: SecurityService,
) : AbstractInternalIngestionHookPayloadStorage(storageService, securityService) {

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    override fun store(payload: IngestionHookPayload, source: String?) {
        super.store(payload, source)
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    override fun routing(payload: IngestionHookPayload, routing: String) {
        super.routing(payload, routing)
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    override fun queue(payload: IngestionHookPayload, queue: String) {
        super.queue(payload, queue)
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    override fun start(payload: IngestionHookPayload) {
        super.start(payload)
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    override fun finished(payload: IngestionHookPayload, outcome: IngestionEventProcessingResult) {
        super.finished(payload, outcome)
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    override fun error(payload: IngestionHookPayload, any: Throwable) {
        super.error(payload, any)
    }

}
package net.nemerosa.ontrack.extension.github.ingestion.processing

import io.micrometer.core.instrument.MeterRegistry
import net.nemerosa.ontrack.extension.github.ingestion.GitHubIngestionHookEventNotSupportedException
import net.nemerosa.ontrack.extension.github.ingestion.metrics.IngestionMetrics
import net.nemerosa.ontrack.extension.github.ingestion.metrics.increment
import net.nemerosa.ontrack.extension.github.ingestion.metrics.timeForPayload
import net.nemerosa.ontrack.extension.github.ingestion.payload.IngestionHookPayload
import net.nemerosa.ontrack.extension.github.ingestion.payload.IngestionHookPayloadStorage
import net.nemerosa.ontrack.model.security.SecurityService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class DefaultIngestionHookProcessingService(
    private val meterRegistry: MeterRegistry,
    private val securityService: SecurityService,
    private val ingestionHookPayloadStorage: IngestionHookPayloadStorage,
    ingestionEventProcessors: List<IngestionEventProcessor>,
) : IngestionHookProcessingService {

    private val eventProcessors = ingestionEventProcessors.associateBy { it.event }

    override fun process(payload: IngestionHookPayload) {
        ingestionHookPayloadStorage.start(payload)
        meterRegistry.increment(payload, IngestionMetrics.PROCESSING_STARTED_COUNT)
        securityService.asAdmin {
            try {
                meterRegistry.timeForPayload(payload, IngestionMetrics.PROCESSING_TIME) {
                    doProcess(payload)
                }
                meterRegistry.increment(payload, IngestionMetrics.PROCESSING_SUCCESS_COUNT)
                ingestionHookPayloadStorage.finished(payload)
            } catch (any: Throwable) {
                ingestionHookPayloadStorage.error(payload, any)
            } finally {
                meterRegistry.increment(payload, IngestionMetrics.PROCESSING_FINISHED_COUNT)
            }
        }
    }

    private fun doProcess(payload: IngestionHookPayload) {
        // Gets the processor for this event
        val eventProcessor = eventProcessors[payload.gitHubEvent]
            ?: throw GitHubIngestionHookEventNotSupportedException(payload.gitHubEvent)
        // Delegates the processing
        eventProcessor.process(payload)
    }

}
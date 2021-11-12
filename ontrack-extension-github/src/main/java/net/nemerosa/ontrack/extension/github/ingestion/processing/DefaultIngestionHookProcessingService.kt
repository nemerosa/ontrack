package net.nemerosa.ontrack.extension.github.ingestion.processing

import io.micrometer.core.instrument.MeterRegistry
import net.nemerosa.ontrack.extension.github.ingestion.GitHubIngestionHookEventNotSupportedException
import net.nemerosa.ontrack.extension.github.ingestion.metrics.IngestionMetrics
import net.nemerosa.ontrack.extension.github.ingestion.metrics.increment
import net.nemerosa.ontrack.extension.github.ingestion.metrics.timeForPayload
import net.nemerosa.ontrack.extension.github.ingestion.payload.IngestionHookPayload
import net.nemerosa.ontrack.extension.github.ingestion.payload.IngestionHookPayloadStorage
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.NameDescription
import net.nemerosa.ontrack.model.support.ApplicationLogEntry
import net.nemerosa.ontrack.model.support.ApplicationLogService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class DefaultIngestionHookProcessingService(
    private val meterRegistry: MeterRegistry,
    private val securityService: SecurityService,
    private val ingestionHookPayloadStorage: IngestionHookPayloadStorage,
    private val applicationLogService: ApplicationLogService,
    ingestionEventProcessors: List<IngestionEventProcessor>,
) : IngestionHookProcessingService {

    private val eventProcessors = ingestionEventProcessors.associateBy { it.event }

    override fun process(payload: IngestionHookPayload) {
        ingestionHookPayloadStorage.start(payload)
        meterRegistry.increment(payload, IngestionMetrics.Process.startedCount)
        securityService.asAdmin {
            try {
                meterRegistry.timeForPayload(payload, IngestionMetrics.Process.time) {
                    val outcome = doProcess(payload)
                    val metric = when (outcome) {
                        IngestionEventProcessingResult.PROCESSED -> IngestionMetrics.Process.successCount
                        IngestionEventProcessingResult.IGNORED -> IngestionMetrics.Process.ignoredCount
                    }
                    meterRegistry.increment(payload, metric)
                }
                ingestionHookPayloadStorage.finished(payload)
            } catch (any: Throwable) {
                meterRegistry.increment(payload, IngestionMetrics.Process.errorCount)
                ingestionHookPayloadStorage.error(payload, any)
                applicationLogService.log(
                    ApplicationLogEntry.error(
                        any,
                        NameDescription.nd("github-ingestion-error", "Error while ingesting data from GitHub"),
                        any.message
                    ).withDetail(
                        "github-event", payload.gitHubEvent
                    ).withDetail(
                        "github-delivery", payload.gitHubDelivery
                    )
                )
            } finally {
                meterRegistry.increment(payload, IngestionMetrics.Process.finishedCount)
            }
        }
    }

    private fun doProcess(payload: IngestionHookPayload): IngestionEventProcessingResult {
        // Gets the processor for this event
        val eventProcessor = eventProcessors[payload.gitHubEvent]
            ?: throw GitHubIngestionHookEventNotSupportedException(payload.gitHubEvent)
        // Delegates the processing
        return eventProcessor.process(payload)
    }

}
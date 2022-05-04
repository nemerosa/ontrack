package net.nemerosa.ontrack.extension.github.ingestion.queue

import io.micrometer.core.instrument.MeterRegistry
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import net.nemerosa.ontrack.extension.github.ingestion.metrics.IngestionMetrics
import net.nemerosa.ontrack.extension.github.ingestion.metrics.increment
import net.nemerosa.ontrack.extension.github.ingestion.payload.IngestionHookPayload
import net.nemerosa.ontrack.extension.github.ingestion.processing.IngestionHookProcessingService
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

/**
 * Synchronous processing, used for testing.
 */
@Component
@ConditionalOnProperty(
    prefix = "ontrack.extension.github.ingestion.processing",
    name = ["async"],
    havingValue = "false",
    matchIfMissing = false,
)
class SyncIngestionHookQueue(
    private val ingestionHookProcessingService: IngestionHookProcessingService,
    private val meterRegistry: MeterRegistry,
) : IngestionHookQueue {
    override fun queue(payload: IngestionHookPayload) {
        meterRegistry.increment(payload, IngestionMetrics.Queue.producedCount)
        runBlocking {
            launch(Job()) {
                meterRegistry.increment(payload, IngestionMetrics.Queue.consumedCount)
                ingestionHookProcessingService.process(payload)
            }
        }
    }
}
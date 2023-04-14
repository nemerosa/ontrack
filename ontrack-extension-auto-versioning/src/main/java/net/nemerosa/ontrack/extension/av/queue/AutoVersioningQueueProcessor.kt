package net.nemerosa.ontrack.extension.av.queue

import net.nemerosa.ontrack.extension.av.audit.AutoVersioningRecordingsExtension
import net.nemerosa.ontrack.extension.av.dispatcher.AutoVersioningOrder
import net.nemerosa.ontrack.extension.av.metrics.AutoVersioningMetricsService
import net.nemerosa.ontrack.extension.av.processing.AutoVersioningProcessingService
import net.nemerosa.ontrack.extension.queue.QueueProcessor
import net.nemerosa.ontrack.extension.recordings.RecordingsQueryService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import kotlin.reflect.KClass

@Component
class AutoVersioningQueueProcessor(
        private val recordingsQueryService: RecordingsQueryService,
        private val autoVersioningMetricsService: AutoVersioningMetricsService,
        private val autoVersioningRecordingsExtension: AutoVersioningRecordingsExtension,
        private val autoVersioningProcessingService: AutoVersioningProcessingService,
) : QueueProcessor<AutoVersioningOrder> {

    private val logger: Logger = LoggerFactory.getLogger(AutoVersioningQueueProcessor::class.java)

    override val id: String = "auto-versioning"

    override val payloadType: KClass<AutoVersioningOrder> = AutoVersioningOrder::class

    override fun process(payload: AutoVersioningOrder) {
        autoVersioningMetricsService.onReceiving(payload)
        val entry = recordingsQueryService.findById(autoVersioningRecordingsExtension, payload.uuid)
        if (entry == null) {
            error("No audit entry found upon receiving the processing order")
        } else if (!entry.mostRecentState.state.isRunning) {
            logger.debug("Cancelled order, not processing. {}", entry)
            return
        }
        autoVersioningProcessingService.process(payload)
    }

    override fun getSpecificRoutingKey(payload: AutoVersioningOrder): String? {
        // TODO Specific routing keys
        return null
    }

    /**
     * Using the target branch ID.
     */
    override fun getRoutingIdentifier(payload: AutoVersioningOrder): String = payload.branch.id.toString()
}
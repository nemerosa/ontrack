package net.nemerosa.ontrack.extension.av.queue

import net.nemerosa.ontrack.extension.av.AutoVersioningConfigProperties
import net.nemerosa.ontrack.extension.av.audit.AutoVersioningAuditQueryService
import net.nemerosa.ontrack.extension.av.audit.AutoVersioningAuditService
import net.nemerosa.ontrack.extension.av.metrics.AutoVersioningMetricsService
import net.nemerosa.ontrack.extension.av.processing.AutoVersioningProcessingService
import net.nemerosa.ontrack.extension.queue.QueueAckMode
import net.nemerosa.ontrack.extension.queue.QueueMetadata
import net.nemerosa.ontrack.extension.queue.QueueProcessor
import net.nemerosa.ontrack.model.security.SecurityService
import org.springframework.stereotype.Component
import kotlin.reflect.KClass

@Component
class AutoVersioningQueueProcessor(
    autoVersioningConfigProperties: AutoVersioningConfigProperties,
    private val autoVersioningAuditQueryService: AutoVersioningAuditQueryService,
    private val metrics: AutoVersioningMetricsService,
    private val securityService: SecurityService,
    private val autoVersioningAuditService: AutoVersioningAuditService,
    private val autoVersioningProcessingService: AutoVersioningProcessingService,
) : QueueProcessor<AutoVersioningQueuePayload> {

    override val id: String = "auto-versioning"

    override val payloadType: KClass<AutoVersioningQueuePayload> = AutoVersioningQueuePayload::class

    override val defaultScale: Int? = autoVersioningConfigProperties.queue.scale

    override val minConcurrency: Int = autoVersioningConfigProperties.queue.minConcurrency
    override val maxConcurrency: Int = autoVersioningConfigProperties.queue.maxConcurrency

    /**
     * Always ack the auto-versioning requests.
     */
    override val ackMode: QueueAckMode = QueueAckMode.IMMEDIATE

    override fun isCancelled(payload: AutoVersioningQueuePayload): String? {
        val order = payload.order
        val entry = autoVersioningAuditQueryService.findByUUID(order.branch, order.uuid)
        return if (entry == null) {
            return "No audit entry found upon receiving the processing order"
        } else if (!entry.mostRecentState.state.isRunning) {
            "Cancelled order, not processing"
        } else {
            null
        }
    }

    override fun process(payload: AutoVersioningQueuePayload, queueMetadata: QueueMetadata?) {
        val order = payload.order
        val queue = queueMetadata?.queueName
        metrics.onReceiving(order, queue)
        securityService.asAdmin {
            autoVersioningAuditService.onReceived(order, queue ?: "n/a")
            val outcome = metrics.processingTiming(order, queue) {
                autoVersioningProcessingService.process(order)
            }
            metrics.onProcessingCompleted(order, outcome)
        }
    }

    override fun getRoutingIdentifier(payload: AutoVersioningQueuePayload): String =
        payload.order.uuid

}
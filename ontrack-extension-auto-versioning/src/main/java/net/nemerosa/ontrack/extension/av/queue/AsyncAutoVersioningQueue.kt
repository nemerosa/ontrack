package net.nemerosa.ontrack.extension.av.queue

import net.nemerosa.ontrack.extension.av.AutoVersioningConfigProperties
import net.nemerosa.ontrack.extension.av.audit.AutoVersioningAuditService
import net.nemerosa.ontrack.extension.av.dispatcher.AutoVersioningOrder
import net.nemerosa.ontrack.extension.av.metrics.AutoVersioningMetricsService
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.format
import org.springframework.amqp.core.AmqpTemplate
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

/**
 * Asynchronous processing, used for production.
 */
@Component
@ConditionalOnProperty(
    prefix = "ontrack.extension.auto-versioning.queue",
    name = ["async"],
    havingValue = "true",
    matchIfMissing = true,
)
class AsyncAutoVersioningQueue(
    private val amqpTemplate: AmqpTemplate,
    private val autoVersioningConfigProperties: AutoVersioningConfigProperties,
    private val autoVersioningAuditService: AutoVersioningAuditService,
    private val metrics: AutoVersioningMetricsService,
) : AutoVersioningQueue {
    override fun queue(order: AutoVersioningOrder) {
        val routingKey = AsyncAutoVersioningQueueConfig.getRoutingKey(autoVersioningConfigProperties, order)
        // Audit
        autoVersioningAuditService.onQueuing(order, routingKey, cancelling = autoVersioningConfigProperties.queue.cancelling)
        // Metrics
        metrics.onQueuing(order, routingKey)
        // Raw message to post
        val message = order.asJson().format()
        amqpTemplate.convertAndSend(
            AsyncAutoVersioningQueueConfig.TOPIC,
            routingKey,
            message,
        )
    }
}
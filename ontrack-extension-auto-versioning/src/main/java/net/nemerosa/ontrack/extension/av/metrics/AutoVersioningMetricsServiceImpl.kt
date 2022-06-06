package net.nemerosa.ontrack.extension.av.metrics

import io.micrometer.core.instrument.MeterRegistry
import net.nemerosa.ontrack.extension.av.dispatcher.AutoVersioningOrder
import net.nemerosa.ontrack.model.metrics.increment
import org.springframework.stereotype.Component

@Component
class AutoVersioningMetricsServiceImpl(
    private val meterRegistry: MeterRegistry,
) : AutoVersioningMetricsService {

    override fun onQueuing(order: AutoVersioningOrder, routingKey: String) {
        meterRegistry.increment(
            order,
            AutoVersioningMetrics.Queue.producedCount,
            AutoVersioningMetrics.Tags.ROUTING_KEY to routingKey,
        )
    }

    override fun onReceiving(order: AutoVersioningOrder, queue: String?) {
        meterRegistry.increment(
            order,
            AutoVersioningMetrics.Queue.consumedCount,
            AutoVersioningMetrics.Tags.QUEUE to (queue ?: "-"),
        )
    }

    private fun MeterRegistry.increment(
        order: AutoVersioningOrder,
        metric: String,
        vararg extraTags: Pair<String, String>,
    ) {
        val tags = listOf(
            "sourceProject" to order.sourceProject,
            "targetProject" to order.branch.project.name,
            "targetBranch" to order.branch.name,
        ) + extraTags.toList()
        increment(
            metric,
            *tags.toTypedArray()
        )
    }
}
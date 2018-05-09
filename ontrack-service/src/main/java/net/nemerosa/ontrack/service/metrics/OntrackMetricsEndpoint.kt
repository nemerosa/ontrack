package net.nemerosa.ontrack.service.metrics

import io.micrometer.core.instrument.MeterRegistry
import net.nemerosa.ontrack.common.Time
import org.springframework.boot.actuate.endpoint.AbstractEndpoint
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class OntrackMetricsEndpoint(
        private val meterRegistry: MeterRegistry
) : AbstractEndpoint<OntrackMetricsCollection>(
        "ontrack_metrics",
        true,
        true
) {
    override fun invoke(): OntrackMetricsCollection {
        val metrics = mutableListOf<OntrackMetricsItem>()
        meterRegistry.forEachMeter { meter ->
            val name: String = meter.id.name
            if (name.startsWith("ontrack")) {
                val tags: Map<String, String> = meter.id.tags
                        .associateBy(
                                { tag -> tag.key },
                                { tag -> tag.value }
                        )
                // Gets the first value
                val value = meter.measure()
                        .firstOrNull()
                        ?.value
                // Metric item
                metrics.add(
                        OntrackMetricsItem(
                                name,
                                tags,
                                value
                        )
                )
            }
        }
        return OntrackMetricsCollection(
                Time.now(),
                metrics
        )
    }
}

class OntrackMetricsCollection(
        val timestamp: LocalDateTime,
        val metrics: List<OntrackMetricsItem>
)

class OntrackMetricsItem(
        val name: String,
        val tags: Map<String, String>,
        val value: Double?
)

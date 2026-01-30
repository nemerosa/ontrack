package net.nemerosa.ontrack.extension.av.audit

import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Tag
import jakarta.annotation.PostConstruct
import net.nemerosa.ontrack.extension.av.metrics.AutoVersioningMetrics
import org.springframework.stereotype.Component

@Component
class AutoVersioningAuditMetricsCollection(
    private val meterRegistry: MeterRegistry,
    private val autoVersioningAuditStore: AutoVersioningAuditStore,
) {

    @PostConstruct
    fun init() {
        // For each possible state
        AutoVersioningAuditState.entries.forEach { state ->
            meterRegistry.gauge(
                AutoVersioningMetrics.States.stateCount,
                listOf(
                    Tag.of("state", state.name)
                ),
                this
            ) {
                autoVersioningAuditStore.countByState(state).toDouble()
            }
        }
    }

}
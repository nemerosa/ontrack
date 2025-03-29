package net.nemerosa.ontrack.extension.av.audit

import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Tag
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Component

@Component
class AutoVersioningAuditMetricsCollection(
    private val meterRegistry: MeterRegistry,
    private val autoVersioningAuditStoreHelper: AutoVersioningAuditStoreHelper,
) {

    @PostConstruct
    fun init() {
        // For each possible state
        AutoVersioningAuditState.values().forEach { state ->
            meterRegistry.gauge(
                AutoVersioningAuditMetrics.autoVersioningAuditState,
                listOf(
                    Tag.of("state", state.name)
                ),
                this
            ) {
                autoVersioningAuditStoreHelper.countByState(state).toDouble()
            }
        }
    }

}
package net.nemerosa.ontrack.extension.av.audit

import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Tag
import net.nemerosa.ontrack.extension.recordings.RecordingsQueryService
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

@Component
class AutoVersioningAuditMetricsCollection(
        private val meterRegistry: MeterRegistry,
        private val autoVersioningRecordingsExtension: AutoVersioningRecordingsExtension,
        private val recordingsQueryService: RecordingsQueryService,
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
                recordingsQueryService.countByFilter(
                        extension = autoVersioningRecordingsExtension,
                        filter = AutoVersioningAuditQueryFilter(
                                state = state
                        )
                ).toDouble()
            }
        }
    }

}
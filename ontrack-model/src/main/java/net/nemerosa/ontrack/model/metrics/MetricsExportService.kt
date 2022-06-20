package net.nemerosa.ontrack.model.metrics

import java.time.LocalDateTime

interface MetricsExportService {

    fun batchExportMetrics(metrics: Collection<Metric>)

    fun exportMetrics(
        metric: String,
        tags: Map<String, String>,
        fields: Map<String, *>,
        timestamp: LocalDateTime? = null,
    )

}
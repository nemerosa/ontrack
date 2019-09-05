package net.nemerosa.ontrack.model.metrics

import java.time.LocalDateTime

interface MetricsExportService {

    fun exportMetrics(
            metric: String,
            tags: Map<String, String>,
            fields: Map<String, Double>,
            timestamp: LocalDateTime? = null
    )

}
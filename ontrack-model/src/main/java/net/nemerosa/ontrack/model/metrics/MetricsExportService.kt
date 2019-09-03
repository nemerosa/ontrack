package net.nemerosa.ontrack.model.metrics

interface MetricsExportService {

    fun exportMetrics(
            metric: String,
            tags: Map<String, String>,
            fields: Map<String, Double>
    )

}
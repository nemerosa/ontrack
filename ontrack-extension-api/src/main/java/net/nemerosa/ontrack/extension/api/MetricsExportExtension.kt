package net.nemerosa.ontrack.extension.api

import net.nemerosa.ontrack.model.extension.Extension
import net.nemerosa.ontrack.model.metrics.Metric
import java.time.LocalDateTime

/**
 * This extension exports some metrics to an external system.
 */
interface MetricsExportExtension : Extension {

    fun batchExportMetrics(metrics: Collection<Metric>)

    fun exportMetrics(
            metric: String,
            tags: Map<String, String>,
            fields: Map<String, *>,
            timestamp: LocalDateTime?
    )

}
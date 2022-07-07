package net.nemerosa.ontrack.extension.api

import net.nemerosa.ontrack.model.extension.Extension
import net.nemerosa.ontrack.model.metrics.Metric
import java.time.LocalDateTime

/**
 * This extension exports some metrics to an external system.
 */
interface MetricsExportExtension : Extension {

    /**
     * Before a complete re-export of metrics is attempted, this method is called to prepare the backend
     * for the new metrics. Like for example, deleting & recreating an index.
     */
    fun prepareReexport()

    fun batchExportMetrics(metrics: Collection<Metric>)

    fun exportMetrics(
            metric: String,
            tags: Map<String, String>,
            fields: Map<String, *>,
            timestamp: LocalDateTime?
    )

}
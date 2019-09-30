package net.nemerosa.ontrack.extension.api

import net.nemerosa.ontrack.model.extension.Extension
import java.time.LocalDateTime

/**
 * This extension exports some metrics to an external system.
 */
interface MetricsExportExtension : Extension {

    fun exportMetrics(
            metric: String,
            tags: Map<String, String>,
            fields: Map<String, Double>,
            timestamp: LocalDateTime?
    )

}
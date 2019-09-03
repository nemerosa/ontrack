package net.nemerosa.ontrack.extension.api

import net.nemerosa.ontrack.model.extension.Extension

/**
 * This extension exports some metrics to an external system.
 */
interface MetricsExportExtension : Extension {

    fun exportMetrics(
            metric: String,
            tags: Map<String, String>,
            fields: Map<String, Double>
    )

}
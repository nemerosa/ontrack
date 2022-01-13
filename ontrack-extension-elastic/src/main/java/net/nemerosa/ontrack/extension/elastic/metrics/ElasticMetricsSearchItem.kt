package net.nemerosa.ontrack.extension.elastic.metrics

import net.nemerosa.ontrack.model.structure.SearchItem
import java.time.LocalDateTime

class ElasticMetricsSearchItem(
        val metric: String,
        val tags: Map<String, String>,
        val values: Map<String, Double>,
        val timestamp: LocalDateTime,
) : SearchItem {
    override val id: String = metric
    override val fields: Map<String, Any?> = mapOf(
            "metric" to metric,
            "tags" to tags,
            "values" to values,
            "timestamp" to timestamp,
    )
}
package net.nemerosa.ontrack.extension.elastic.metrics

import net.nemerosa.ontrack.model.structure.SearchNodeResults

interface ElasticMetricsClient {

    fun saveMetric(entry: ECSEntry)

    @Deprecated("Use saveMetrics for ECS")
    fun saveMetric(metric: String, data: Map<String, Any>)

    fun rawSearch(
        token: String,
        indexName: String?,
        offset: Int = 0,
        size: Int = 10,
    ): SearchNodeResults

}
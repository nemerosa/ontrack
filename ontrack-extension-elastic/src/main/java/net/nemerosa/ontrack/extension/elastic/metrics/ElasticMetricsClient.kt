package net.nemerosa.ontrack.extension.elastic.metrics

interface ElasticMetricsClient {

    fun saveMetric(metric: String, data: Map<String, Any>)

}
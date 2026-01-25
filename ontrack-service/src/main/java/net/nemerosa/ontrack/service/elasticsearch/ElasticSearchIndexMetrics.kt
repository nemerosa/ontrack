package net.nemerosa.ontrack.service.elasticsearch

import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.annotations.APIName
import net.nemerosa.ontrack.model.metrics.MetricsDocumentation
import net.nemerosa.ontrack.model.metrics.MetricsMeterDocumentation
import net.nemerosa.ontrack.model.metrics.MetricsMeterTag
import net.nemerosa.ontrack.model.metrics.MetricsMeterType

@Suppress("ConstPropertyName")
@MetricsDocumentation
@APIName("Elasticsearch index metrics")
@APIDescription("Metrics related to the population of Elasticsearch indices.")
object ElasticSearchIndexMetrics {

    const val METRIC_INDEX = "index"

    /**
     * Prefix for all metric names
     */
    private const val prefix = "ontrack_elasticsearch"

    @APIDescription("Duration of a full reindex.")
    @MetricsMeterDocumentation(
        type = MetricsMeterType.TIMER,
        tags = [
            MetricsMeterTag(METRIC_INDEX, "Name of the index"),
        ]
    )
    const val esIndexAll = "${prefix}_index_all"
}
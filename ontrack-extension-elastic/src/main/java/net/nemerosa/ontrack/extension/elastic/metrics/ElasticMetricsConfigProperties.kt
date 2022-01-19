package net.nemerosa.ontrack.extension.elastic.metrics

import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@ConfigurationProperties(prefix = ElasticMetricsConfigProperties.ELASTIC_METRICS_PREFIX)
@Component
class ElasticMetricsConfigProperties {

    companion object {
        /**
         * Prefix for the Elastic metrics configuration.
         */
        const val ELASTIC_METRICS_PREFIX = "ontrack.extension.elastic.metrics"
    }

    /**
     * Is the export of metrics to Elastic enabled?
     */
    var enabled: Boolean = false

    /**
     * Defines where the Elastic metrics should be sent.
     */
    var target: ElasticMetricsTarget = ElasticMetricsTarget.MAIN

    /**
     * Index properties
     */
    var index = IndexConfigProperties()

    /**
     * Custom connection
     */
    var custom = ElasticsearchProperties()

    /**
     * Index properties
     */
    class IndexConfigProperties {

        /**
         * Name of the index to contains all Ontrack metrics
         */
        var name = "ontrack_metrics"

        /**
         * Prefix to use for index names
         */
        var prefix = "ontrack_metric"

        /**
         * Flag to enable immediate re-indexation after items are added into the index (used mostly
         * for testing).
         */
        var immediate = false

    }
}

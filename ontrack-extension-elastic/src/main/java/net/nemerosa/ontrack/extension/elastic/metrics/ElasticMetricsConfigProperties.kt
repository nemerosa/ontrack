package net.nemerosa.ontrack.extension.elastic.metrics

import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.convert.DurationUnit
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.temporal.ChronoUnit

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
     * Must we trace the behaviour of the export of the metrics in the logs?
     */
    var debug: Boolean = false

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
     * Set to true to enable the API Compatibility mode when accessing a 8.x ES server.
     *
     * See https://www.elastic.co/guide/en/elasticsearch/client/java-rest/7.17/java-rest-high-compatibility.html
     */
    var apiCompatibilityMode: Boolean = false

    /**
     * Index properties
     */
    class IndexConfigProperties {

        /**
         * Name of the index to contains all Ontrack metrics
         */
        var name = "ontrack_metrics"

        /**
         * Flag to enable immediate re-indexation after items are added into the index (used mostly
         * for testing).
         */
        var immediate = false

    }

    /**
     * Queue configuration
     */
    var queue = QueueConfigProperties()

    /**
     * Queue configuration
     */
    class QueueConfigProperties {

        /**
         * Maximum capacity for the queue
         */
        var capacity: UInt = 1024U

        /**
         * Maximum buffer for the queue before flushing
         */
        var buffer: UInt = 512U

        /**
         * Interval between the regular flushing of the queue of events
         */
        @DurationUnit(ChronoUnit.MINUTES)
        var flushing: Duration = Duration.ofMinutes(1)

    }
}

package net.nemerosa.ontrack.extension.elastic.metrics

import net.nemerosa.ontrack.extension.api.MetricsExportExtension
import net.nemerosa.ontrack.extension.elastic.ElasticExtensionFeature
import net.nemerosa.ontrack.extension.support.AbstractExtension
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.stereotype.Component
import java.time.LocalDateTime

/**
 * Exports metrics to Elastic using the general Elastic Spring Boot metric management configuration.
 */
@Component
@ConditionalOnBean(ElasticMetricsClient::class)
class ElasticMetricsExportExtension(
    extensionFeature: ElasticExtensionFeature,
    private val elasticMetricsClient: ElasticMetricsClient,
) : AbstractExtension(extensionFeature), MetricsExportExtension {

    override fun exportMetrics(
        metric: String,
        tags: Map<String, String>,
        fields: Map<String, Double>,
        timestamp: LocalDateTime?,
    ) {
        if (timestamp != null) {
            elasticMetricsClient.saveMetric(
                metric = metric,
                data = mapOf(
                    "tags" to tags,
                    "fields" to fields,
                    "timestamp" to timestamp,
                )
            )
        }
    }

}
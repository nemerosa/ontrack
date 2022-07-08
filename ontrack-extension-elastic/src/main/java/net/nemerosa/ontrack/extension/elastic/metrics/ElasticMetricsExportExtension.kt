package net.nemerosa.ontrack.extension.elastic.metrics

import net.nemerosa.ontrack.extension.api.MetricsExportExtension
import net.nemerosa.ontrack.extension.elastic.ElasticExtensionFeature
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.metrics.Metric
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

    override fun prepareReexport() {
        elasticMetricsClient.dropIndex()
    }

    override fun batchExportMetrics(metrics: Collection<Metric>) {
        elasticMetricsClient.saveMetrics(
            metrics.map { metric ->
                metric.toECSEntry()
            }
        )
    }

    override fun exportMetrics(
        metric: String,
        tags: Map<String, String>,
        fields: Map<String, *>,
        timestamp: LocalDateTime?,
    ) {
        if (timestamp != null) {
            batchExportMetrics(
                listOf(
                    Metric(
                        metric = metric,
                        tags = tags,
                        fields = fields,
                        timestamp = timestamp
                    )
                )
            )
        }
    }

}
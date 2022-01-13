package net.nemerosa.ontrack.extension.elastic.metrics

import net.nemerosa.ontrack.extension.api.MetricsExportExtension
import net.nemerosa.ontrack.extension.elastic.ElasticExtensionFeature
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.structure.SearchIndexService
import org.springframework.stereotype.Component
import java.time.LocalDateTime

/**
 * Exports metrics to Elastic using the general Elastic Spring Boot metric management configuration.
 */
@Component
class ElasticMetricsExportExtension(
        extensionFeature: ElasticExtensionFeature,
        private val indexer: ElasticMetricsSearchIndexer,
        private val searchIndexService: SearchIndexService,
) : AbstractExtension(extensionFeature), MetricsExportExtension {

    override fun exportMetrics(metric: String, tags: Map<String, String>, fields: Map<String, Double>, timestamp: LocalDateTime?) {
        if (timestamp != null) {
            searchIndexService.createSearchIndex(
                    indexer,
                    ElasticMetricsSearchItem(
                            metric = metric,
                            tags = tags,
                            values = fields,
                            timestamp = timestamp,
                    )
            )
        }
    }

}
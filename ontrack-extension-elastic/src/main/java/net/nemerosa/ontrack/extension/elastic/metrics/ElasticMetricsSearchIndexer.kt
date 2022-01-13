package net.nemerosa.ontrack.extension.elastic.metrics

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.elastic.ElasticExtensionFeature
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.structure.*
import org.springframework.stereotype.Component

@Component
class ElasticMetricsSearchIndexer(
        extensionFeature: ElasticExtensionFeature,
) : AbstractExtension(extensionFeature), SearchIndexer<ElasticMetricsSearchItem> {

    override val indexerName: String = "Elastic metrics"

    override val indexName: String = "ontrack_metrics"

    override val indexMapping: SearchIndexMapping = indexMappings<ElasticMetricsSearchItem> {
        +ElasticMetricsSearchItem::metric to keyword()
        +ElasticMetricsSearchItem::tags to `object`()
        +ElasticMetricsSearchItem::values to `object`()
        +ElasticMetricsSearchItem::timestamp to timestamp()
    }

    /**
     * Not doing anything since this indexer is not supposed to run in the background.
     */
    override fun indexAll(processor: (ElasticMetricsSearchItem) -> Unit) {}

    /**
     * Returning null since this indexer is not eligible for search.
     */
    override val searchResultType: SearchResultType? = null

    /**
     * Returning null since this indexer is not eligible for search.
     */
    override fun toSearchResult(id: String, score: Double, source: JsonNode): SearchResult? = null
}
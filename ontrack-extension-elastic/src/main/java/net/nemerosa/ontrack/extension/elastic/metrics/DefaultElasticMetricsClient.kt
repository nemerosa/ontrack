package net.nemerosa.ontrack.extension.elastic.metrics

import net.nemerosa.ontrack.model.structure.SearchNodeResults
import net.nemerosa.ontrack.model.structure.SearchResultNode
import org.apache.http.HttpHost
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest
import org.elasticsearch.action.index.IndexRequest
import org.elasticsearch.action.search.SearchRequest
import org.elasticsearch.client.RequestOptions
import org.elasticsearch.client.RestClient
import org.elasticsearch.client.RestHighLevelClient
import org.elasticsearch.index.query.MultiMatchQueryBuilder
import org.elasticsearch.search.builder.SearchSourceBuilder
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component
import java.net.URI

@Component
@ConditionalOnProperty(
    prefix = ElasticMetricsConfigProperties.ELASTIC_METRICS_PREFIX,
    name = ["enabled"],
    havingValue = "true",
    matchIfMissing = false,
)
class DefaultElasticMetricsClient(
    private val elasticMetricsConfigProperties: ElasticMetricsConfigProperties,
    private val defaultClient: RestHighLevelClient,
) : ElasticMetricsClient {

    override fun saveMetric(entry: ECSEntry) {
        val indexName = elasticMetricsConfigProperties.index.name
        client.index(
            IndexRequest(indexName).source(entry),
            RequestOptions.DEFAULT
        )
        // Refreshes the index if needed
        immediateRefreshIfRequested(indexName)
    }

    override fun saveMetric(metric: String, data: Map<String, Any>) {
        val indexName = "${elasticMetricsConfigProperties.index.prefix}_$metric"
        client.index(
            IndexRequest(indexName).source(data),
            RequestOptions.DEFAULT
        )
        // Refreshes the index
        immediateRefreshIfRequested(indexName)
    }


    override fun rawSearch(
        token: String,
        indexName: String?,
        offset: Int,
        size: Int,
    ): SearchNodeResults {
        val esRequest = SearchRequest().source(
            SearchSourceBuilder().query(
                MultiMatchQueryBuilder(token).type(MultiMatchQueryBuilder.Type.BEST_FIELDS)
            ).from(
                offset
            ).size(
                size
            )
        ).run {
            indexName?.let {
                indices(indexName)
            } ?: this
        }

        // Getting the result of the search
        val response = client.search(esRequest, RequestOptions.DEFAULT)

        // Pagination information
        val responseHits = response.hits
        val totalHits = responseHits.totalHits?.value ?: 0

        // Hits as JSON nodes
        val hits = responseHits.hits.map {
            SearchResultNode(
                it.index,
                it.id,
                it.score.toDouble(),
                it.sourceAsMap
            )
        }

        // Transforming into search results
        return SearchNodeResults(
            items = hits,
            offset = offset,
            total = totalHits.toInt(),
            message = when {
                totalHits <= 0 -> "The number of total matches is not known and pagination is not possible."
                else -> null
            }
        )
    }


    private fun immediateRefreshIfRequested(indexName: String) {
        if (elasticMetricsConfigProperties.index.immediate) {
            val refreshRequest = RefreshRequest(indexName)
            client.indices().refresh(refreshRequest, RequestOptions.DEFAULT)
        }
    }

    private val client: RestHighLevelClient by lazy {
        when (elasticMetricsConfigProperties.target) {
            // Using the main ES instance
            ElasticMetricsTarget.MAIN -> defaultClient
            // Using the custom ES instance
            ElasticMetricsTarget.CUSTOM -> customClient()
        }
    }

    private fun customClient(): RestHighLevelClient {
        val hosts = elasticMetricsConfigProperties.custom.uris.map { value ->
            val uri = URI(value)
            HttpHost.create(
                URI(
                    uri.scheme, null, uri.host, uri.port, uri.path, uri.query, uri.fragment
                ).toString()
            )
        }
        val builder = RestClient.builder(*hosts.toTypedArray())
        if (elasticMetricsConfigProperties.custom.pathPrefix != null) {
            builder.setPathPrefix(elasticMetricsConfigProperties.custom.pathPrefix)
        }
        return RestHighLevelClient(builder)
    }
}
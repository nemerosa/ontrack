package net.nemerosa.ontrack.extension.elastic.metrics

import org.apache.http.HttpHost
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest
import org.elasticsearch.action.index.IndexRequest
import org.elasticsearch.client.RequestOptions
import org.elasticsearch.client.RestClient
import org.elasticsearch.client.RestHighLevelClient
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

    override fun saveMetric(metric: String, data: Map<String, Any>) {
        val indexName = "${elasticMetricsConfigProperties.index.prefix}_$metric"
        client.index(
            IndexRequest(indexName).source(data),
            RequestOptions.DEFAULT
        )
        // Refreshes the index
        immediateRefreshIfRequested(indexName)
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
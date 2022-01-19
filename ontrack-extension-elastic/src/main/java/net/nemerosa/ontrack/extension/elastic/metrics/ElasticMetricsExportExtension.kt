package net.nemerosa.ontrack.extension.elastic.metrics

import net.nemerosa.ontrack.extension.api.MetricsExportExtension
import net.nemerosa.ontrack.extension.elastic.ElasticExtensionFeature
import net.nemerosa.ontrack.extension.support.AbstractExtension
import org.apache.http.HttpHost
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest
import org.elasticsearch.action.index.IndexRequest
import org.elasticsearch.client.RequestOptions
import org.elasticsearch.client.RestClient
import org.elasticsearch.client.RestHighLevelClient
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component
import java.net.URI
import java.time.LocalDateTime

/**
 * Exports metrics to Elastic using the general Elastic Spring Boot metric management configuration.
 */
@Component
@ConditionalOnProperty(
    prefix = ElasticMetricsConfigProperties.ELASTIC_METRICS_PREFIX,
    name = ["enabled"],
    havingValue = "true",
    matchIfMissing = false,
)
class ElasticMetricsExportExtension(
    extensionFeature: ElasticExtensionFeature,
    private val elasticMetricsConfigProperties: ElasticMetricsConfigProperties,
    private val defaultClient: RestHighLevelClient,
) : AbstractExtension(extensionFeature), MetricsExportExtension {

    override fun exportMetrics(
        metric: String,
        tags: Map<String, String>,
        fields: Map<String, Double>,
        timestamp: LocalDateTime?,
    ) {
        if (timestamp != null) {
            val indexName = "${elasticMetricsConfigProperties.index.prefix}_$metric"
            val doc = mapOf(
                "tags" to tags,
                "fields" to fields,
            )
            client.index(
                IndexRequest(indexName).source(doc),
                RequestOptions.DEFAULT
            )
            // Refreshes the index
            immediateRefreshIfRequested(indexName)
        }
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
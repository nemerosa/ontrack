package net.nemerosa.ontrack.extension.elastic.metrics

import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.binder.MeterBinder
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.toJsonMap
import net.nemerosa.ontrack.model.metrics.increment
import net.nemerosa.ontrack.model.structure.SearchNodeResults
import net.nemerosa.ontrack.model.structure.SearchResultNode
import net.nemerosa.ontrack.model.support.time
import org.apache.http.HttpHost
import org.apache.http.auth.AuthScope
import org.apache.http.auth.Credentials
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.impl.client.BasicCredentialsProvider
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest
import org.elasticsearch.action.bulk.BulkRequest
import org.elasticsearch.action.index.IndexRequest
import org.elasticsearch.action.search.SearchRequest
import org.elasticsearch.client.RequestOptions
import org.elasticsearch.client.RestClient
import org.elasticsearch.client.RestHighLevelClient
import org.elasticsearch.client.RestHighLevelClientBuilder
import org.elasticsearch.index.query.MultiMatchQueryBuilder
import org.elasticsearch.search.builder.SearchSourceBuilder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component
import java.net.URI
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

@DelicateCoroutinesApi
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
) : ElasticMetricsClient, MeterBinder {

    private val logger: Logger = LoggerFactory.getLogger(DefaultElasticMetricsClient::class.java)

    private var registry: MeterRegistry? = null

    private fun debug(message: String) {
        if (elasticMetricsConfigProperties.debug && logger.isDebugEnabled) {
            logger.debug(message)
        }
    }

    /**
     * Internal in-memory queue
     */
    private val queue = Channel<ECSEntry>(capacity = elasticMetricsConfigProperties.queue.capacity.toInt())

    /**
     * Channels do not have a measurable size, so keeping our own count.
     */
    private val queueSize = AtomicLong()

    /**
     * Buffer of entries
     */
    private val buffer = ConcurrentLinkedQueue<ECSEntry>()

    /**
     * Buffer lock
     */
    private val bufferLock = ReentrantLock()

    override fun bindTo(registry: MeterRegistry) {
        this.registry = registry
        // Size of the queue
        registry.gauge(ElasticMetricsClientMetrics.queue, this) {
            queueSize.get().toDouble()
        }
        // Size of the buffer
        registry.gauge(ElasticMetricsClientMetrics.buffer, this) {
            buffer.size.toDouble()
        }
    }

    override fun saveMetrics(entries: Collection<ECSEntry>) {
        try {
            if (elasticMetricsConfigProperties.index.immediate) {
                // Direct registration
                val indexName = elasticMetricsConfigProperties.index.name
                entries.forEach { entry ->
                    val source = entry.asJson().toJsonMap()
                    client.index(
                        IndexRequest(indexName).id(entry.computeId()).source(source),
                        RequestOptions.DEFAULT
                    )
                }
                // ... and flushing immediately
                val refreshRequest = RefreshRequest(indexName)
                client.indices().refresh(refreshRequest, RequestOptions.DEFAULT)
            } else {
                runBlocking {
                    launch {
                        entries.forEach { entry ->
                            queueSize.incrementAndGet()
                            queue.send(entry)
                        }
                    }
                }
            }
        } catch (ex: Throwable) {
            logger.error("Cannot queue ${entries.size} metrics for export to ElasticSearch", ex)
        }
    }

    override fun saveMetric(entry: ECSEntry) {
        saveMetrics(listOf(entry))
    }

    init {
        /**
         * Launching the queue processing at startup
         */
        GlobalScope.launch {
            receiveEvents()
        }
        /**
         * Launching the queue regular purging
         */
        GlobalScope.launch {
            flushEvents()
        }
    }

    /**
     * Processing of the events
     */
    private suspend fun receiveEvents() {
        while (true) {
            try {
                val entry = queue.receive()
                queueSize.decrementAndGet()
                debug("Entry received: $entry")
                runBlocking {
                    launch(Job()) {
                        processEvent(entry)
                    }
                }
            } catch (any: Exception) {
                logger.error("Error on dequeuing an ECS entry", any)
            }
        }
    }

    private fun processEvent(entry: ECSEntry) {
        buffer.add(entry)
        debug("Entry buffered (${buffer.size}/${elasticMetricsConfigProperties.queue.buffer}) $entry")
        if (buffer.size >= elasticMetricsConfigProperties.queue.buffer.toInt()) {
            // Flushing the buffer
            debug("Buffer flushing")
            flushing()
        }
    }

    /**
     * Flushing the events regularly
     */
    private suspend fun flushEvents() {
        runBlocking {
            while (true) {
                try {
                    // Wait between each flushing session
                    delay(elasticMetricsConfigProperties.queue.flushing.toMillis())
                    // Flushing
                    debug("Timed flushing")
                    flushing()
                } catch (any: Exception) {
                    logger.error("Error on timed flushing", any)
                }
            }
        }
    }

    private fun flushing() {
        debug("Trying to get lock on buffer")
        bufferLock.withLock { }
        if (bufferLock.tryLock(1, TimeUnit.MINUTES)) {
            debug("Locked on the buffer")
            try {
                if (buffer.isNotEmpty()) {
                    debug("Flushing all entries (${buffer.size})")
                    // Copy of the elements
                    val entries = buffer.toTypedArray()
                    // Flushing the buffer
                    buffer.clear()
                    // Creating the bulk request
                    val indexName = elasticMetricsConfigProperties.index.name
                    val request = BulkRequest()
                    entries.forEach {
                        val source = it.asJson().toJsonMap()
                        request.add(IndexRequest(indexName).id(it.computeId()).source(source))
                    }
                    // Bulk request execution
                    registry?.time(ElasticMetricsClientMetrics.time) {
                        try {
                            client.bulk(request, RequestOptions.DEFAULT)
                        } catch (any: Exception) {
                            logger.error("Cannot export ${entries.size} metrics to ElasticSearch", any)
                            registry?.increment(ElasticMetricsClientMetrics.errors)
                        }
                    }
                } else {
                    debug("Nothing to flush")
                }
            } finally {
                bufferLock.unlock()
                debug("Unlocking the buffer")
            }
        } else {
            debug("Could not acquire lock. Skipping.")
        }
    }

    override fun dropIndex() {
        if (elasticMetricsConfigProperties.allowDrop) {
            logger.info("Dropping the ${elasticMetricsConfigProperties.index.name} index before re-export")
            client.indices().delete(
                DeleteIndexRequest(elasticMetricsConfigProperties.index.name),
                RequestOptions.DEFAULT
            )
        }
    }

    override fun rawSearch(
        token: String,
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
        ).indices(elasticMetricsConfigProperties.index.name)

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
        if (!elasticMetricsConfigProperties.custom.username.isNullOrBlank()) {
            builder.setHttpClientConfigCallback {
                it.setDefaultCredentialsProvider(
                    BasicCredentialsProvider().apply {
                        val credentials: Credentials = UsernamePasswordCredentials(
                            elasticMetricsConfigProperties.custom.username,
                            elasticMetricsConfigProperties.custom.password
                        )
                        setCredentials(AuthScope.ANY, credentials)
                    }
                )
            }
        }
        if (elasticMetricsConfigProperties.custom.pathPrefix != null) {
            builder.setPathPrefix(elasticMetricsConfigProperties.custom.pathPrefix)
        }
        val client = builder.build()
        return RestHighLevelClientBuilder(client)
            .setApiCompatibilityMode(elasticMetricsConfigProperties.apiCompatibilityMode)
            .build()
    }
}
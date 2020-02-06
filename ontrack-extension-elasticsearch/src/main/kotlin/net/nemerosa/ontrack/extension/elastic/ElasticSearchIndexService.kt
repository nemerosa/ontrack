package net.nemerosa.ontrack.extension.elastic

import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.model.support.OntrackConfigProperties
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest
import org.elasticsearch.action.bulk.BulkRequest
import org.elasticsearch.action.delete.DeleteRequest
import org.elasticsearch.action.index.IndexRequest
import org.elasticsearch.client.RequestOptions
import org.elasticsearch.client.RestHighLevelClient
import org.elasticsearch.client.indices.CreateIndexRequest
import org.elasticsearch.client.indices.GetIndexRequest
import org.elasticsearch.client.indices.PutMappingRequest
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
@ConditionalOnProperty(
        name = [OntrackConfigProperties.SEARCH_ENGINE_PROPERTY],
        havingValue = ElasticSearchConfigProperties.SEARCH_ENGINE_ELASTICSEARCH
)
class ElasticSearchIndexService(
        private val client: RestHighLevelClient,
        private val ontrackConfigProperties: OntrackConfigProperties
) : SearchIndexService {

    private val logger: Logger = LoggerFactory.getLogger(ElasticSearchIndexService::class.java)

    private fun <T : SearchItem> refreshIndex(indexer: SearchIndexer<T>) {
        logger.debug("Refreshing index ${indexer.indexName}")
        val refreshRequest = RefreshRequest(indexer.indexName)
        client.indices().refresh(refreshRequest, RequestOptions.DEFAULT)
    }

    override fun <T : SearchItem> index(indexer: SearchIndexer<T>) {
        val batchSize = indexer.indexBatch ?: ontrackConfigProperties.search.index.batch
        logger.debug("Full indexation for ${indexer.indexName} with batch size = $batchSize")
        val buffer = mutableListOf<T>()
        indexer.indexAll { item ->
            buffer.add(item)
            if (buffer.size == batchSize) {
                index(indexer, buffer)
                buffer.clear()
            }
        }
        // Remaining items
        index(indexer, buffer)
        // Refreshes the index
        immediateRefreshIfRequested(indexer)
    }

    override fun <T : SearchItem> initIndex(indexer: SearchIndexer<T>) {
        logger.info("[elasticsearch][index][${indexer.indexName}] Init")
        val indexExists = client.indices().exists(GetIndexRequest(indexer.indexName), RequestOptions.DEFAULT)
        if (indexExists) {
            logger.info("[elasticsearch][index][${indexer.indexName}] Already exists")
            indexer.indexMapping?.let { mapping ->
                val mappingSource = mappingToMap(mapping)
                logger.info("[elasticsearch][index][${indexer.indexName}] Updating mapping: $mappingSource")
                val request = PutMappingRequest(indexer.indexName).source(mappingSource)
                client.indices().putMapping(request, RequestOptions.DEFAULT)
            }
        } else {
            logger.info("[elasticsearch][index][${indexer.indexName}] Creating index")
            val request = CreateIndexRequest(indexer.indexName).run {
                indexer.indexMapping?.let {
                    val mappingSource = mappingToMap(it)
                    logger.info("[elasticsearch][index][${indexer.indexName}] Mapping=$mappingSource")
                    mapping(mappingSource)
                } ?: this
            }
            client.indices().create(request, RequestOptions.DEFAULT)
        }
    }

    /**
     * Converts a generic mapping into an ElasticSearch mapping.
     */
    private fun mappingToMap(mapping: SearchIndexMapping): Map<String, Any> {
        return mapOf(
                "properties" to mapping.fields
                        .filter { it.types.isNotEmpty() }
                        .associate { fieldMapping ->
                            // Property mapping
                            val property = mutableMapOf<String, Any>()
                            // Primary type
                            val primary = fieldMapping.types[0]
                            setType(property, primary, nested = false)
                            // Other fields
                            if (fieldMapping.types.size > 1) {
                                val fields = mutableMapOf<String, Any>()
                                property["fields"] = fields
                                fieldMapping.types.drop(1)
                                        .forEach { type ->
                                            if (!type.type.isNullOrBlank()) {
                                                val typeMap = mutableMapOf<String, Any>()
                                                fields[type.type!!] = typeMap
                                                setType(typeMap, type, nested = true)
                                            }
                                        }
                            }
                            // OK
                            fieldMapping.name to property
                        }
        )
    }

    private fun setType(property: MutableMap<String, Any>, type: SearchIndexMappingFieldType, nested: Boolean) {
        type.type?.let { property["type"] = it }
        type.index?.let { property["index"] = it }
        if (!nested) {
            type.scoreBoost?.let { property["boost"] = it }
        }
    }

    override fun <T : SearchItem> resetIndex(indexer: SearchIndexer<T>, reindex: Boolean): Boolean {
        // Deletes the index
        client.indices().delete(DeleteIndexRequest(indexer.indexName), RequestOptions.DEFAULT)
        // Re-creates the index
        initIndex(indexer)
        // Re-index if requested
        if (reindex) {
            index(indexer)
        }
        // Refreshes the index always
        refreshIndex(indexer)
        // OK
        return true
    }

    private fun <T : SearchItem> index(indexer: SearchIndexer<T>, items: List<T>) {
        // Bulk indexation of the items
        val bulk = items.fold(BulkRequest(indexer.indexName)) { acc, item ->
            acc.add(IndexRequest().id(item.id).source(item.fields))
        }
        // Launching the indexation of this batch
        client.bulk(bulk, RequestOptions.DEFAULT)
        // Refreshes the index
        immediateRefreshIfRequested(indexer)
    }

    private fun <T : SearchItem> immediateRefreshIfRequested(indexer: SearchIndexer<T>) {
        if (ontrackConfigProperties.search.index.immediate) {
            refreshIndex(indexer)
        }
    }

    override val searchIndexesAvailable: Boolean = true

    override fun <T : SearchItem> createSearchIndex(indexer: SearchIndexer<T>, item: T) {
        logger.debug("Create index ${indexer.indexName}")
        client.index(
                IndexRequest(indexer.indexName).id(item.id).source(item.fields),
                RequestOptions.DEFAULT
        )
        // Refreshes the index
        immediateRefreshIfRequested(indexer)
    }

    override fun <T : SearchItem> updateSearchIndex(indexer: SearchIndexer<T>, item: T) {
        logger.debug("Update index ${indexer.indexName}")
        client.index(
                IndexRequest(indexer.indexName).id(item.id).source(item.fields),
                RequestOptions.DEFAULT
        )
    }

    override fun <T : SearchItem> deleteSearchIndex(indexer: SearchIndexer<T>, id: String) {
        logger.debug("Delete index ${indexer.indexName}")
        client.delete(
                DeleteRequest(indexer.indexName).id(id),
                RequestOptions.DEFAULT
        )
        // Refreshes the index
        immediateRefreshIfRequested(indexer)
    }

}

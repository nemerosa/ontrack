package net.nemerosa.ontrack.extension.elastic

import io.searchbox.client.JestClient
import io.searchbox.core.Bulk
import io.searchbox.core.Delete
import io.searchbox.core.Index
import io.searchbox.indices.CreateIndex
import io.searchbox.indices.DeleteIndex
import io.searchbox.indices.Refresh
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.model.support.OntrackConfigProperties
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
        private val jestClient: JestClient,
        private val ontrackConfigProperties: OntrackConfigProperties
) : SearchIndexService {

    private val logger: Logger = LoggerFactory.getLogger(ElasticSearchIndexService::class.java)

    private fun <T : SearchItem> refreshIndex(indexer: SearchIndexer<T>) {
        logger.debug("Refreshing index ${indexer.indexName}")
        Refresh.Builder().addIndex(indexer.indexName).build().apply {
            jestClient.execute(this).checkResult()
        }
    }

    override fun <T : SearchItem> index(indexer: SearchIndexer<T>) {
        val batchSize = 1000 // TODO Make the batch size configurable
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
        val builder = CreateIndex.Builder(indexer.indexName).run {
            indexer.indexMapping?.let {
                val mapping = mappingToMap(it)
                logger.info("[elasticsearch][index][${indexer.indexName}] Mapping=$mapping")
                this.mappings(mappingToMap(it))
            } ?: this
        }
        jestClient.execute(builder.build()).checkResult()
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
                            setType(property, primary)
                            // Other fields
                            if (fieldMapping.types.size > 1) {
                                val fields = mutableMapOf<String, Any>()
                                property["fields"] = fields
                                fieldMapping.types.drop(1)
                                        .forEach { type ->
                                            if (!type.type.isNullOrBlank()) {
                                                val typeMap = mutableMapOf<String, Any>()
                                                fields[type.type!!] = typeMap
                                                setType(typeMap, type)
                                            }
                                        }
                            }
                            // OK
                            fieldMapping.name to property
                        }
        )
    }

    private fun setType(property: MutableMap<String, Any>, type: SearchIndexMappingFieldType) {
        type.index?.let { property["index"] = it }
        type.type?.let { property["type"] = it }
        type.scoreBoost?.let { property["boost"] = it }
    }

    override fun <T : SearchItem> resetIndex(indexer: SearchIndexer<T>, reindex: Boolean): Boolean {
        // Deletes the index
        jestClient.execute(DeleteIndex.Builder(indexer.indexName).build()).checkResult()
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
        val bulk = Bulk.Builder().defaultIndex(indexer.indexName)
                .addAction(
                        items.map { item ->
                            Index.Builder(item.fields)
                                    .id(item.id)
                                    .build()
                        }
                )
                .build()
        // Launching the indexation of this batch
        jestClient.execute(bulk).checkResult()
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
        jestClient.execute(
                Bulk.Builder().addAction(
                        Index.Builder(item.fields)
                                .index(indexer.indexName)
                                .id(item.id)
                                .build()
                ).build()
        ).checkResult()
        // Refreshes the index
        immediateRefreshIfRequested(indexer)
    }

    override fun <T : SearchItem> updateSearchIndex(indexer: SearchIndexer<T>, item: T) {
        logger.debug("Update index ${indexer.indexName}")
        jestClient.execute(
                Index.Builder(item.fields)
                        .index(indexer.indexName)
                        .id(item.id)
                        .build()
        ).checkResult()
    }

    override fun <T : SearchItem> deleteSearchIndex(indexer: SearchIndexer<T>, id: String) {
        logger.debug("Delete index ${indexer.indexName}")
        jestClient.execute(
                Bulk.Builder().addAction(
                        Delete.Builder(id)
                                .index(indexer.indexName)
                                .build()
                ).build()
        ).checkResult()
        // Refreshes the index
        immediateRefreshIfRequested(indexer)
    }

}

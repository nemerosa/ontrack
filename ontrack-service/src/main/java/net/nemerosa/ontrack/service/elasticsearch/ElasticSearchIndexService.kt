package net.nemerosa.ontrack.service.elasticsearch

import co.elastic.clients.elasticsearch.ElasticsearchClient
import co.elastic.clients.elasticsearch._types.ElasticsearchException
import co.elastic.clients.elasticsearch._types.mapping.Property
import co.elastic.clients.elasticsearch._types.mapping.PropertyBase
import co.elastic.clients.elasticsearch.core.BulkRequest
import co.elastic.clients.elasticsearch.core.DeleteRequest
import co.elastic.clients.elasticsearch.core.GetRequest
import co.elastic.clients.elasticsearch.core.IndexRequest
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest
import co.elastic.clients.elasticsearch.indices.DeleteIndexRequest
import co.elastic.clients.elasticsearch.indices.ExistsRequest
import co.elastic.clients.elasticsearch.indices.RefreshRequest
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.model.support.OntrackConfigProperties
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
@Transactional
class ElasticSearchIndexService(
    private val client: ElasticsearchClient,
    private val ontrackConfigProperties: OntrackConfigProperties
) : SearchIndexService {

    private val logger: Logger = LoggerFactory.getLogger(ElasticSearchIndexService::class.java)

    private fun <T : SearchItem> refreshIndex(indexer: SearchIndexer<T>) {
        logger.debug("Refreshing index ${indexer.indexName}")
        val refreshRequest = RefreshRequest.Builder().index(indexer.indexName).build()
        client.indices().refresh(refreshRequest)
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
        val indexExists = client.indices().exists(ExistsRequest.Builder().index(indexer.indexName).build()).value()
        if (!indexExists) {
            logger.info("[elasticsearch][index][${indexer.indexName}] Creating index")
            val request = CreateIndexRequest.Builder().index(indexer.indexName).run {
                indexer.indexMapping?.let { indexMapping ->
                    mappings { typeMappingBuilder ->
                        indexMapping.fields
                            .filter { it.types.isNotEmpty() }
                            .forEach { fieldMapping ->
                                typeMappingBuilder.properties(fieldMapping.name) { propBuilder ->
                                    setupProperty(fieldMapping, propBuilder)
                                }
                            }
                        typeMappingBuilder
                    }
                } ?: this
            }.build()

            try {
                client.indices().create(request)
            } catch (ex: ElasticsearchException) {
                if (ontrackConfigProperties.search.index.ignoreExisting) {
                    logger.info("[elasticsearch][index][${indexer.indexName}] Cannot create index because already existing. Ignoring issue (likely happens during test).")
                } else {
                    throw ex
                }
            }
        }
    }

    private fun setupProperty(
        fieldMapping: SearchIndexMappingField,
        propBuilder: Property.Builder
    ): Property.Builder {
        val primary = fieldMapping.types[0]
        setType(propBuilder, primary) { baseBuilder ->
            // Other types
            if (fieldMapping.types.size > 1) {
                fieldMapping.types.drop(1)
                    .forEach { type ->
                        if (!type.type.isNullOrBlank()) {
                            baseBuilder.fields(type.type) { builder ->
                                setType(builder, type) {}
                                builder
                            }
                        }
                    }
            }
        }
        // OK
        return propBuilder
    }

    private fun setType(
        propBuilder: Property.Builder,
        type: SearchIndexMappingFieldType,
        baseBuilderCode: (PropertyBase.AbstractBuilder<*>) -> Unit,
    ) {
        if (type.type != null) {
            when (type.type) {
                "long" -> propBuilder.long_ { typeBuilder ->
                    type.index?.let { typeBuilder.index(it) }
                    baseBuilderCode(typeBuilder)
                    typeBuilder
                }

                "keyword" -> propBuilder.keyword { typeBuilder ->
                    type.index?.let { typeBuilder.index(it) }
                    baseBuilderCode(typeBuilder)
                    typeBuilder
                }

                "object" -> propBuilder.`object` { typeBuilder ->
                    baseBuilderCode(typeBuilder)
                    typeBuilder
                }

                "date" -> propBuilder.date { typeBuilder ->
                    type.index?.let { typeBuilder.index(it) }
                    baseBuilderCode(typeBuilder)
                    typeBuilder
                }

                "text" -> propBuilder.text { typeBuilder ->
                    type.index?.let { typeBuilder.index(it) }
                    baseBuilderCode(typeBuilder)
                    typeBuilder
                }

                "nested" -> propBuilder.nested { typeBuilder ->
                    baseBuilderCode(typeBuilder)
                    typeBuilder
                }

                else -> error("Unknown type ${type.type}")
            }
        } else {
            error("Missing required type")
        }
    }

    override fun <T : SearchItem> resetIndex(indexer: SearchIndexer<T>, reindex: Boolean): Boolean {
        // Deletes the index
        client.indices().delete(DeleteIndexRequest.Builder().index(indexer.indexName).build())
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
        val bulkRequestBuilder = BulkRequest.Builder().index(indexer.indexName)
        var operationsCount = 0
        items.forEach { item ->
            val operation = BulkOperation.Builder()
                .index<Any?> { op ->
                    op
                        .id(item.id)
                        .index(indexer.indexName)
                        .document(item.fields)
                }.build()
            bulkRequestBuilder.operations(operation)
            operationsCount++
        }
        if (operationsCount > 0) {
            val bulkRequest = bulkRequestBuilder.build()
            // Launching the indexation of this batch
            client.bulk(bulkRequest)
            // Refreshes the index
            immediateRefreshIfRequested(indexer)
        }
    }

    private fun <T : SearchItem> immediateRefreshIfRequested(indexer: SearchIndexer<T>) {
        if (ontrackConfigProperties.search.index.immediate) {
            refreshIndex(indexer)
        }
    }

    override val searchIndexesAvailable: Boolean = true

    override fun <T : SearchItem> createSearchIndex(indexer: SearchIndexer<T>, item: T) {
        logger.debug("Create index ${indexer.indexName}")
        val indexRequest = IndexRequest.Builder<Any>()
            .index(indexer.indexName)
            .id(item.id)
            .document(item.fields)
            .build()
        client.index(indexRequest)
        // Refreshes the index
        immediateRefreshIfRequested(indexer)
    }

    override fun <T : SearchItem> updateSearchIndex(indexer: SearchIndexer<T>, item: T) {
        logger.debug("Update index ${indexer.indexName}")
        val indexRequest = IndexRequest.Builder<Any>()
            .index(indexer.indexName)
            .id(item.id)
            .document(item.fields)
            .build()
        client.index(indexRequest)
    }

    override fun <T : SearchItem> deleteSearchIndex(indexer: SearchIndexer<T>, id: String) {
        logger.debug("Delete index ${indexer.indexName}")
        val deleteRequest = DeleteRequest.Builder().index(indexer.indexName).id(id).build()
        client.delete(deleteRequest)
        // Refreshes the index
        immediateRefreshIfRequested(indexer)
    }

    override fun <T : SearchItem> batchSearchIndex(
        indexer: SearchIndexer<T>,
        items: Collection<T>,
        mode: BatchIndexMode
    ): BatchIndexResults {
        logger.debug("[search][batch-index] index={},items={},mode={}", indexer.indexName, items.size, mode)
        // Building the list of actions to take
        val bulk = items.fold(BatchSearchIndexBulk(indexer.indexName)) { acc, item ->
            val action = batchSearchIndexAction(indexer, item, mode)
            if (ontrackConfigProperties.search.index.logging && logger.isDebugEnabled) {
                if (action.action != null) {
                    logger.debug("[search][batch-index] index=${indexer.indexName},item=${item.id},mode=$mode,action=${action.action::class.java.simpleName}")
                } else if (ontrackConfigProperties.search.index.tracing) {
                    logger.debug("[search][batch-index] index=${indexer.indexName},item=${item.id},mode=$mode,action=none")
                }
            }
            acc + action
        }
        // Launching the indexation of this batch
        val bulkRequest = bulk.bulk.build()
        logger.info("[search][batch-index] index=${indexer.indexName},items=${items.size},mode=$mode,actions=${bulkRequest.operations().size}")
        if (bulkRequest.operations().size > 0) {
            client.bulk(bulkRequest)
            // Refreshes the index
            immediateRefreshIfRequested(indexer)
        }
        // OK
        return bulk.results
    }

    private fun <T : SearchItem> batchSearchIndexAction(
        indexer: SearchIndexer<T>,
        item: T,
        mode: BatchIndexMode
    ): BatchSearchIndexAction {
        // Gets the existing item using its ID
        val getRequest = GetRequest.Builder()
            .index(indexer.indexName)
            .id(item.id)
            .build()
        val response = client.get(getRequest, Any::class.java)
        // If item exists
        return if (response.found()) {
            when (mode) {
                BatchIndexMode.KEEP -> BatchSearchIndexAction(null, BatchIndexResults.KEEP)
                BatchIndexMode.UPDATE -> BatchSearchIndexAction(
                    BulkOperation.Builder().index<Any> { indexBuilder ->
                        indexBuilder.index(indexer.indexName)
                        indexBuilder.id(item.id)
                        indexBuilder.document(item.fields)
                    }.build(),
                    BatchIndexResults.UPDATE
                )
            }
        }
        // If not existing
        else {
            BatchSearchIndexAction(
                BulkOperation.Builder().index<Any> { indexBuilder ->
                    indexBuilder.index(indexer.indexName)
                    indexBuilder.id(item.id)
                    indexBuilder.document(item.fields)
                }.build(),
                BatchIndexResults.ADD
            )
        }
    }

    private class BatchSearchIndexBulk private constructor(
        val bulk: BulkRequest.Builder,
        val results: BatchIndexResults
    ) {
        constructor(indexName: String) : this(
            BulkRequest.Builder().index(indexName),
            BatchIndexResults.NONE
        )

        operator fun plus(action: BatchSearchIndexAction) =
            BatchSearchIndexBulk(
                bulk = action.action?.let {
                    bulk.operations(it)
                } ?: bulk,
                results = results + action.results
            )
    }

    private class BatchSearchIndexAction(
        val action: BulkOperation?,
        val results: BatchIndexResults
    )

}

package net.nemerosa.ontrack.extension.elastic

import io.searchbox.client.JestClient
import io.searchbox.core.Bulk
import io.searchbox.core.Delete
import io.searchbox.core.Index
import io.searchbox.core.Search
import io.searchbox.indices.CreateIndex
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.asJsonString
import net.nemerosa.ontrack.json.parseAsJson
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.model.support.OntrackConfigProperties
import net.nemerosa.ontrack.model.support.StartupService
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
@ConditionalOnProperty(
        name = [OntrackConfigProperties.SEARCH_PROPERTY],
        havingValue = ElasticSearchConfigProperties.SEARCH_SERVICE_ELASTICSEARCH
)
class ElasticSearchServiceImpl(
        private val jestClient: JestClient,
        private val searchProviders: List<SearchProvider>
) : SearchService, ElasticSearchService, StartupService, SearchIndexService {

    override fun search(request: SearchRequest): Collection<SearchResult> {
        val query = mapOf(
                "query" to mapOf(
                        "multi_match" to mapOf(
                                "query" to request.token,
                                "type" to "best_fields"
                        )
                )
        ).asJson().asJsonString()

        val search = Search.Builder(query).build()

        val result = jestClient.execute(search).jsonString.parseAsJson()

        TODO("ElasticSearch search to be implemented")
    }

    override fun <T : SearchItem> index(indexer: SearchIndexer<T>) {
        val batchSize = 1000 // TODO Make the batch size configurable
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
        jestClient.execute(bulk)
    }

    override val searchIndexesAvailable: Boolean = true

    override fun <T : SearchItem> createSearchIndex(indexer: SearchIndexer<T>, item: T) {
        jestClient.execute(
                Index.Builder(item.fields)
                        .index(indexer.indexName)
                        .id(item.id)
                        .build()
        )
    }

    override fun <T : SearchItem> updateSearchIndex(indexer: SearchIndexer<T>, item: T) =
            createSearchIndex(indexer, item)

    override fun <T : SearchItem> deleteSearchIndex(indexer: SearchIndexer<T>, id: String) {
        jestClient.execute(
                Delete.Builder(id)
                        .index(indexer.indexName)
                        .build()
        )
    }

    override fun getName(): String = "Creation of ElasticSearch indexes"

    override fun startupOrder(): Int = StartupService.JOB_REGISTRATION - 1 // Just before the jobs

    override fun start() {
        searchProviders.forEach { provider ->
            provider.searchIndexers.map { indexer ->
                val action = CreateIndex.Builder(indexer.indexName).build()
                jestClient.execute(action)
            }
        }
    }

}

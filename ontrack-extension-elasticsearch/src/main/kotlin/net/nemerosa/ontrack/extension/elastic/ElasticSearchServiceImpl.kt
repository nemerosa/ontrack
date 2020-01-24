package net.nemerosa.ontrack.extension.elastic

import io.searchbox.client.JestClient
import io.searchbox.core.Bulk
import io.searchbox.core.Index
import io.searchbox.indices.CreateIndex
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
) : SearchService, ElasticSearchService, StartupService {

    override fun search(request: SearchRequest): Collection<SearchResult> {
        TODO("ElasticSearch search to be implemented")
    }

    override fun <T : SearchItem> index(indexer: SearchIndexer<T>) {
        val batchSize = 1000 // TODO Make the batch size configurable
        val items = indexer.indexation()
        items.chunked(batchSize).forEach { batch ->
            index(indexer, batch)
        }
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
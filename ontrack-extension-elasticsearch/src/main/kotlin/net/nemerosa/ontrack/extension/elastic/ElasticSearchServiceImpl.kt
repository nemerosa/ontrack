package net.nemerosa.ontrack.extension.elastic

import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.model.support.OntrackConfigProperties
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
@ConditionalOnProperty(
        name = [OntrackConfigProperties.SEARCH_PROPERTY],
        havingValue = ElasticSearchConfigProperties.SEARCH_SERVICE_ELASTICSEARCH
)
class ElasticSearchServiceImpl : SearchService, ElasticSearchService {

    override fun search(request: SearchRequest): Collection<SearchResult> {
        TODO("ElasticSearch search to be implemented")
    }

    override fun <T : SearchItem> index(indexer: SearchIndexer<T>) {
        val batchSize = 1000 // TODO Make the batch size configurable
        val index = indexer.createFullIndex()
        val items = index.items()
        items.chunked(batchSize).forEach { batch ->
            index(index, batch)
        }
    }

    private fun <T : SearchItem> index(index: SearchIndex<T>, items: List<T>) {
        TODO("Indexation")
    }

}
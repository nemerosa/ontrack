package net.nemerosa.ontrack.extension.elastic

import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.model.Ack
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.model.support.OntrackConfigProperties
import org.elasticsearch.client.RequestOptions
import org.elasticsearch.client.RestHighLevelClient
import org.elasticsearch.index.query.MultiMatchQueryBuilder
import org.elasticsearch.search.builder.SearchSourceBuilder
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

typealias ESSearchRequest = org.elasticsearch.action.search.SearchRequest

@Service
@Transactional
@ConditionalOnProperty(
        name = [OntrackConfigProperties.SEARCH_ENGINE_PROPERTY],
        havingValue = ElasticSearchConfigProperties.SEARCH_ENGINE_ELASTICSEARCH
)
class ElasticSearchServiceImpl(
        private val client: RestHighLevelClient,
        private val searchProviders: List<SearchProvider>,
        private val searchIndexService: SearchIndexService
) : SearchService {

    val indexers: Map<String, SearchIndexer<*>> by lazy {
        searchProviders.flatMap { it.searchIndexers }.associateBy { it.indexName }
    }

    override fun search(request: SearchRequest): Collection<SearchResult> {
        val esRequest = ESSearchRequest().source(
                SearchSourceBuilder().query(
                        MultiMatchQueryBuilder(request.token).type(MultiMatchQueryBuilder.Type.BEST_FIELDS)
                )
        )

        // Getting the result of the search
        val response = client.search(esRequest, RequestOptions.DEFAULT)

        // Hits as JSON nodes
        val hits = response.hits.hits.map {
            HitNode(
                    it.index,
                    it.id,
                    it.score.toDouble(),
                    it.sourceAsMap
            )
        }

        // Transforming into search results
        return hits.mapNotNull { toResult(it) }
    }

    override fun indexReset(reindex: Boolean): Ack {
        val ok = indexers.all { (_, indexer) ->
            searchIndexService.resetIndex(indexer, reindex)
        }
        return Ack(ok)
    }

    override fun indexInit() {
        indexers.forEach { (_, indexer) -> searchIndexService.initIndex(indexer) }
    }

    private fun toResult(hitNode: HitNode): SearchResult? {
        // Gets the indexer
        val indexer = indexers[hitNode.index]
        // Transformation
        return indexer?.let { toResult(hitNode, it) }
    }

    private fun <T : SearchItem> toResult(hitNode: HitNode, indexer: SearchIndexer<T>): SearchResult? =
            indexer.toSearchResult(
                    hitNode.id,
                    hitNode.score,
                    hitNode.source.asJson()
            )

    private class HitNode(
            val index: String,
            val id: String,
            val score: Double,
            val source: Map<String, Any?>
    )

}

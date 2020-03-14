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
        private val searchIndexers: List<SearchIndexer<*>>,
        private val searchIndexService: SearchIndexService
) : SearchService {

    val indexers: Map<String, SearchIndexer<*>> by lazy {
        searchIndexers.associateBy { it.indexName }
    }

    val indexerByResultType: Map<String, SearchIndexer<*>> by lazy {
        searchIndexers.associateBy { it.searchResultType.id }
    }

    override fun paginatedSearch(request: SearchRequest): SearchResults {
        val esRequest = ESSearchRequest().source(
                SearchSourceBuilder().query(
                        MultiMatchQueryBuilder(request.token).type(MultiMatchQueryBuilder.Type.BEST_FIELDS)
                ).from(
                        request.offset
                ).size(
                        request.size
                )
        ).run {
            request.type?.let { type ->
                indexerByResultType[type]?.let { indexer -> indices(indexer.indexName) }
            } ?: this
        }

        // Getting the result of the search
        val response = client.search(esRequest, RequestOptions.DEFAULT)

        // Pagination information
        val responseHits = response.hits
        val totalHits = responseHits.totalHits?.value ?: 0

        // Hits as JSON nodes
        val hits = responseHits.hits.map {
            HitNode(
                    it.index,
                    it.id,
                    it.score.toDouble(),
                    it.sourceAsMap
            )
        }

        // Transforming into search results
        return SearchResults(
                items = hits.mapNotNull { toResult(it) },
                offset = request.offset,
                total = totalHits.toInt(),
                message = when {
                    totalHits <= 0 -> "The number of total matches is not known and pagination is not possible."
                    else -> null
                }
        )
    }

    override val searchResultTypes: List<SearchResultType>
        get() =
            indexers
                    .map { (_, indexer) -> indexer.searchResultType }
                    .sortedBy { it.name }

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

}

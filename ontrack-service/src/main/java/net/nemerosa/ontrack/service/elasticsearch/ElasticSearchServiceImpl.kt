package net.nemerosa.ontrack.service.elasticsearch

import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.model.Ack
import net.nemerosa.ontrack.model.structure.*
import org.elasticsearch.client.RequestOptions
import org.elasticsearch.client.RestHighLevelClient
import org.elasticsearch.index.query.MultiMatchQueryBuilder
import org.elasticsearch.search.builder.SearchSourceBuilder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

typealias ESSearchRequest = org.elasticsearch.action.search.SearchRequest

@Service
@Transactional
class ElasticSearchServiceImpl(
        private val client: RestHighLevelClient,
        private val searchIndexers: List<SearchIndexer<*>>,
        private val searchIndexService: SearchIndexService
) : SearchService {

    val indexers: Map<String, SearchIndexer<*>> by lazy {
        searchIndexers.associateBy { it.indexName }
    }

    val indexerByResultType: Map<String, SearchIndexer<*>> by lazy {
        searchIndexers.filter { it.searchResultType != null }.associateBy { it.searchResultType!!.id }
    }

    override fun paginatedSearch(request: SearchRequest): SearchResults = rawSearch(
            token = request.token,
            indexName = request.type?.let { type ->
                indexerByResultType[type]?.indexName
            },
            offset = request.offset,
            size = request.size,
    ).run {
        SearchResults(
                items = items.mapNotNull { toResult(it) },
                offset = offset,
                total = total,
                message = message,
        )
    }

    override fun rawSearch(
            token: String,
            indexName: String?,
            offset: Int,
            size: Int,
    ): SearchNodeResults {
        val esRequest = ESSearchRequest().source(
                SearchSourceBuilder().query(
                        MultiMatchQueryBuilder(token).type(MultiMatchQueryBuilder.Type.BEST_FIELDS)
                ).from(
                        offset
                ).size(
                        size
                )
        ).run {
            indexName?.let {
                indices(indexName)
            } ?: this
        }

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

    override val searchResultTypes: List<SearchResultType>
        get() =
            indexers
                    .mapNotNull { (_, indexer) -> indexer.searchResultType }
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

    private fun toResult(hitNode: SearchResultNode): SearchResult? {
        // Gets the indexer
        val indexer = indexers[hitNode.index]
        // Transformation
        return indexer?.let { toResult(hitNode, it) }
    }

    private fun <T : SearchItem> toResult(hitNode: SearchResultNode, indexer: SearchIndexer<T>): SearchResult? =
            indexer.toSearchResult(
                    hitNode.id,
                    hitNode.score,
                    hitNode.source.asJson()
            )

}

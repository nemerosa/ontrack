package net.nemerosa.ontrack.service.elasticsearch

import co.elastic.clients.elasticsearch.ElasticsearchClient
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders.multiMatch
import co.elastic.clients.elasticsearch._types.query_dsl.TextQueryType
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.model.Ack
import net.nemerosa.ontrack.model.structure.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

typealias ESSearchRequestBuilder = co.elastic.clients.elasticsearch.core.SearchRequest.Builder

@Service
@Transactional
class ElasticSearchServiceImpl(
    private val client: ElasticsearchClient,
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

        val searchRequest = ESSearchRequestBuilder().apply {
            if (indexName != null) {
                index(indexName)
            }
            from(offset)
            size(size)
            multiMatch()
                .query(token)
                .type(TextQueryType.BestFields)
        }.build()

        // Getting the result of the search
        val response = client.search(searchRequest, Map::class.java)

        // Pagination information
        val responseHits = response.hits()
        val totalHits = responseHits.total()?.value() ?: 0

        // Hits as JSON nodes
        @Suppress("UNCHECKED_CAST")
        val hits = responseHits.hits().mapNotNull { hit ->
            val id = hit.id()
            if (id != null) {
                SearchResultNode(
                    index = hit.index(),
                    id = id,
                    score = hit.score() ?: 0.0,
                    source = hit.source() as Map<String, Any?>
                )
            } else {
                null
            }
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
                .sortedBy { it.order }

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

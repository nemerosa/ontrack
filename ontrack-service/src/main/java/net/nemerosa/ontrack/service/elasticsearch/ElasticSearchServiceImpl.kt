package net.nemerosa.ontrack.service.elasticsearch

import co.elastic.clients.elasticsearch.ElasticsearchClient
import co.elastic.clients.elasticsearch._types.ElasticsearchException
import co.elastic.clients.elasticsearch._types.query_dsl.TextQueryType
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.model.Ack
import net.nemerosa.ontrack.model.structure.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory
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

    private val logger: Logger = LoggerFactory.getLogger(ElasticSearchServiceImpl::class.java)

    val indexers: Map<String, SearchIndexer<*>> by lazy {
        searchIndexers.associateBy { it.indexName }
    }

    val indexerByResultType: Map<String, SearchIndexer<*>> by lazy {
        searchIndexers.filter { it.searchResultType != null }.associateBy { it.searchResultType!!.id }
    }

    override fun paginatedSearch(request: SearchRequest): SearchResults {
        val searchIndexer = indexerByResultType[request.type]
            ?: return SearchResults.empty
        return rawSearch(
            token = request.token,
            searchIndexer = searchIndexer,
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
    }

    private fun rawSearch(
        token: String,
        searchIndexer: SearchIndexer<*>,
        offset: Int,
        size: Int,
    ): SearchNodeResults {

        // Compute field boosts from the index mapping
        val fieldBoosts = searchIndexer.indexMapping
            ?.let { mapping ->
                mapping.fields.map { field ->
                    // Get the maximum boost across all types for this field
                    val maxBoost = field.types.mapNotNull { it.scoreBoost }.maxOrNull()
                    if (maxBoost != null && maxBoost > 0.0) {
                        "${field.name}^$maxBoost"
                    } else {
                        field.name
                    }
                }
            } ?: emptyList()

        // If no field mappings are available, return empty results early
        // This prevents multi_match queries on empty indices from failing
        if (fieldBoosts.isEmpty()) {
            logger.debug("No field mappings available for index '${searchIndexer.indexName}', returning empty results")
            return SearchNodeResults(
                items = emptyList(),
                offset = 0,
                total = 0,
                message = "Search index '${searchIndexer.indexName}' has no searchable fields configured."
            )
        }

        val searchRequest = ESSearchRequestBuilder().apply {
            index(searchIndexer.indexName)
            from(offset)
            size(size)
            // Allow partial search results even if some shards fail
            allowPartialSearchResults(true)
            query { q ->
                q.multiMatch { m ->
                    m.query(token)
                        .type(TextQueryType.BestFields)
                        .fields(fieldBoosts)
                        // Lenient mode prevents failures when fields don't exist
                        .lenient(true)
                }
            }
        }.build()

        // Getting the result of the search
        val response = try {
            client.search(searchRequest, Map::class.java)
        } catch (ex: ElasticsearchException) {
            // If search fails (e.g., all shards failed on empty index), return empty results
            logger.warn("Search failed for index '${searchIndexer.indexName}': ${ex.message}")
            return SearchNodeResults(
                items = emptyList(),
                offset = 0,
                total = 0,
                message = "Search temporarily unavailable for this result type: ${searchIndexer.indexerId}"
            )
        }

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

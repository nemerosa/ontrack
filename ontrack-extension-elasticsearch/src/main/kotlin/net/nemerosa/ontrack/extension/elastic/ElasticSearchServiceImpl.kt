package net.nemerosa.ontrack.extension.elastic

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.JsonNode
import io.searchbox.client.JestClient
import io.searchbox.core.Search
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.asJsonString
import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.json.parseAsJson
import net.nemerosa.ontrack.model.Ack
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.model.support.OntrackConfigProperties
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
@ConditionalOnProperty(
        name = [OntrackConfigProperties.SEARCH_ENGINE_PROPERTY],
        havingValue = ElasticSearchConfigProperties.SEARCH_ENGINE_ELASTICSEARCH
)
class ElasticSearchServiceImpl(
        private val jestClient: JestClient,
        private val searchProviders: List<SearchProvider>,
        private val searchIndexService: SearchIndexService
) : SearchService {

    val indexers: Map<String, SearchIndexer<*>> by lazy {
        searchProviders.flatMap { it.searchIndexers }.associateBy { it.indexName }
    }

    override fun search(request: SearchRequest): Collection<SearchResult> {
        // ES query
        val query = mapOf(
                "query" to mapOf(
                        "multi_match" to mapOf(
                                "query" to request.token,
                                "type" to "best_fields"
                        )
                )
        ).asJson().asJsonString()
        val search = Search.Builder(query).build()

        // Getting the result of the search
        val result = jestClient.execute(search).checkResult().jsonString.parseAsJson()

        // Getting the hits
        val hits = result["hits"]["hits"].map { it.parse<HitNode>() }

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
                    hitNode.source
            )

    @JsonIgnoreProperties(ignoreUnknown = true)
    private class HitNode(
            @JsonProperty("_index")
            val index: String,
            @JsonProperty("_id")
            val id: String,
            @JsonProperty("_score")
            val score: Double,
            @JsonProperty("_source")
            val source: JsonNode
    )

}

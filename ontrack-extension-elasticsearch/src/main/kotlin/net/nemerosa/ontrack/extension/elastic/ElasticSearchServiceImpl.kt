package net.nemerosa.ontrack.extension.elastic

import io.searchbox.client.JestClient
import io.searchbox.core.Search
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.asJsonString
import net.nemerosa.ontrack.json.parseAsJson
import net.nemerosa.ontrack.model.structure.SearchProvider
import net.nemerosa.ontrack.model.structure.SearchRequest
import net.nemerosa.ontrack.model.structure.SearchResult
import net.nemerosa.ontrack.model.structure.SearchService
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
class ElasticSearchServiceImpl(
        private val jestClient: JestClient,
        private val searchProviders: List<SearchProvider>
) : SearchService {

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

}

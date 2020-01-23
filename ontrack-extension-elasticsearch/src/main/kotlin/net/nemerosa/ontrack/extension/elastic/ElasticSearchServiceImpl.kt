package net.nemerosa.ontrack.extension.elastic

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
        havingValue = "elasticsearch"
)
class ElasticSearchServiceImpl : SearchService {

    override fun search(request: SearchRequest): Collection<SearchResult> {
        TODO("ElasticSearch search to be implemented")
    }

}
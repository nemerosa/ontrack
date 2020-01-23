package net.nemerosa.ontrack.service

import net.nemerosa.ontrack.model.structure.SearchProvider
import net.nemerosa.ontrack.model.structure.SearchRequest
import net.nemerosa.ontrack.model.structure.SearchResult
import net.nemerosa.ontrack.model.structure.SearchService
import net.nemerosa.ontrack.model.support.OntrackConfigProperties
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Default search service implementation.
 */
@Service
@Transactional
@ConditionalOnProperty(
        name = [OntrackConfigProperties.SEARCH_PROPERTY],
        havingValue = "default",
        matchIfMissing = true
)
class SearchServiceImpl @Autowired constructor(val providers: List<SearchProvider>) : SearchService {

    override fun search(request: SearchRequest): Collection<SearchResult> = providers
            .filter { it.isTokenSearchable(request.token) }
            .flatMap { it.search(request.token) }

}

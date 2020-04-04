package net.nemerosa.ontrack.service

import net.nemerosa.ontrack.model.Ack
import net.nemerosa.ontrack.model.structure.SearchRequest
import net.nemerosa.ontrack.model.structure.SearchResultType
import net.nemerosa.ontrack.model.structure.SearchResults
import net.nemerosa.ontrack.model.structure.SearchService
import net.nemerosa.ontrack.model.support.OntrackConfigProperties
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Default search service implementation.
 */
@Service
@Transactional
@ConditionalOnProperty(
        name = [OntrackConfigProperties.SEARCH_ENGINE_PROPERTY],
        havingValue = "default",
        matchIfMissing = true
)
class SearchServiceImpl : SearchService {

    /**
     * Not available for this type of service
     */
    override val searchResultTypes: List<SearchResultType> = emptyList()

    override fun paginatedSearch(request: SearchRequest): SearchResults = SearchResults(
            emptyList(),
            0, 0,
            null
    )

    /**
     * Not supported in legacy search.
     */
    override fun indexReset(reindex: Boolean): Ack = Ack.NOK

    /**
     * NOP
     */
    override fun indexInit() {
    }
}

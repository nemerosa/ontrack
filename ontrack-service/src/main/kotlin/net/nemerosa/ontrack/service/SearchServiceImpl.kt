package net.nemerosa.ontrack.service

import net.nemerosa.ontrack.model.Ack
import net.nemerosa.ontrack.model.structure.SearchProvider
import net.nemerosa.ontrack.model.structure.SearchRequest
import net.nemerosa.ontrack.model.structure.SearchResult
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
class SearchServiceImpl(
        val providers: List<SearchProvider>
) : SearchService {

    override fun search(request: SearchRequest): Collection<SearchResult> = providers
            .filter { it.isTokenSearchable(request.token) }
            .flatMap { it.search(request.token) }

    /**
     * Not supported in legacy search.
     */
    override fun indexReset(reindex: Boolean): Ack {
        return Ack.NOK;
    }

    /**
     * NOP
     */
    override fun indexInit() {
    }
}

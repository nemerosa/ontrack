package net.nemerosa.ontrack.service

import net.nemerosa.ontrack.model.structure.SearchProvider
import net.nemerosa.ontrack.model.structure.SearchRequest
import net.nemerosa.ontrack.model.structure.SearchResult
import net.nemerosa.ontrack.model.structure.SearchService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class SearchServiceImpl @Autowired constructor(val providers: List<SearchProvider>) : SearchService {

    override fun search(request: SearchRequest): Collection<SearchResult> = providers
            .filter { it.isTokenSearchable(request.token) }
            .flatMap { it.search(request.token) }

}

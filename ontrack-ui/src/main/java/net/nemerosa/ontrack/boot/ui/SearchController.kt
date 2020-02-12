package net.nemerosa.ontrack.boot.ui

import net.nemerosa.ontrack.model.structure.SearchRequest
import net.nemerosa.ontrack.model.structure.SearchResult
import net.nemerosa.ontrack.model.structure.SearchResultType
import net.nemerosa.ontrack.model.structure.SearchService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

/**
 * End point for the search.
 */
@RestController
class SearchController(
        private val searchService: SearchService
) {

    @PostMapping("/search")
    fun search(@RequestBody request: SearchRequest): Collection<SearchResult> = searchService.search(request)

    /**
     * Gets the list of search types
     */
    @GetMapping("/search/types")
    fun getSearchTypes(): List<SearchResultType> = searchService.searchResultTypes

    @PostMapping("/search/index/reset")
    fun searchIndexReset(@RequestBody request: SearchIndexResetRequest) = searchService.indexReset(request.reindex)

    data class SearchIndexResetRequest(val reindex: Boolean = false)

}
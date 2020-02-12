package net.nemerosa.ontrack.model.structure

/**
 * Results being returned by a search.
 *
 * @property items List of items actually returned
 * @property offset Offset of the result list
 * @property total Total number of matches
 * @property message Any message associated with the search
 */
class SearchResults(
        val items: List<SearchResult>,
        val offset: Int,
        val total: Int,
        val message: String?
)

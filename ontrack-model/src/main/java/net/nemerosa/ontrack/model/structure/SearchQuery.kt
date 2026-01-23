package net.nemerosa.ontrack.model.structure

/**
 * This interface provides a way to perform a dedicated search outside Elasticsearch.
 */
interface SearchQuery {

    /**
     * Given a search token, returns search results.
     */
    fun query(
        token: String,
        offset: Int,
        size: Int,
    ): SearchResults

}
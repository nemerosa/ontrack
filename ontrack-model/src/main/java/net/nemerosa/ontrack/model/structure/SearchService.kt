package net.nemerosa.ontrack.model.structure

import net.nemerosa.ontrack.model.Ack

/**
 * Service to search based on text
 */
interface SearchService {

    /**
     * Gets the list of types of search
     */
    val searchResultTypes: List<SearchResultType>

    /**
     * Paginated search entry point based on registered search indexers.
     */
    fun paginatedSearch(request: SearchRequest): SearchResults

    /**
     * Makes sure all search indexes are initialized.
     */
    fun indexInit()

    /**
     * Resetting all search indexes, optionally restoring them.
     *
     *
     * This method is mostly used for testing but could be used
     * to reset faulty indexes.
     *
     * @param reindex `true` to relaunch the indexation afterward
     * @param logErrors `true` to log errors only, not raise exceptions
     * @return OK if indexation was completed successfully
     */
    fun indexReset(reindex: Boolean, logErrors: Boolean): Ack

    /**
     * Launching the indexation for a given result type. Waits until the indexation is completed.
     *
     * @param resultType Result type to index
     */
    fun reindex(resultType: String)

}
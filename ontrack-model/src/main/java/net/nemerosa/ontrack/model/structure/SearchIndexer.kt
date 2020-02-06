package net.nemerosa.ontrack.model.structure

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.job.Schedule

/**
 * Allows a [SearchProvider] to index some search data.
 */
interface SearchIndexer<T : SearchItem> {

    /**
     * ID of this indexer, used to uniquely identify this indexer.
     *
     * Defaults to the qualified class name of this indexer.
     */
    val indexerId: String get() = this::class.java.name

    /**
     * Display name for this indexer.
     */
    val indexerName: String

    /**
     * Is this indexer disabled?
     *
     * By default, `false`, enabled.
     */
    val isIndexerDisabled: Boolean get() = false

    /**
     * Gets the indexation schedule for this indexer.
     *
     * By default, no schedule == manual indexation only
     */
    val indexerSchedule: Schedule get() = Schedule.NONE

    /**
     * Gets the index name
     */
    val indexName: String

    /**
     * Index mapping if defined
     */
    val indexMapping: SearchIndexMapping? get() = null

    /**
     * Number of items to include in a batch when re-indexing a whole collection.
     *
     * If not defined, the default settings will be used instead
     *
     * @see net.nemerosa.ontrack.model.support.SearchConfigProperties.SearchIndexProperties.batch
     */
    val indexBatch: Int? get() = null

    /**
     * Index all elements.
     */
    fun indexAll(processor: (T) -> Unit)

    /**
     * Search result type
     */
    val searchResultType: SearchResultType

    /**
     * Given some raw search results, transform them into a [SearchResult] for display.
     *
     * @param id ID of the entity having being found
     * @param score Score of the item in the search
     * @param source Raw search result, typically a JSON representation of [SearchItem.fields]
     * @return `null` if the [source] cannot be read, cannot be found or is not authorized
     */
    fun toSearchResult(id: String, score: Double, source: JsonNode): SearchResult?

}
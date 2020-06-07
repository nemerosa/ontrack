package net.nemerosa.ontrack.model.structure

import com.fasterxml.jackson.annotation.JsonIgnore
import java.net.URI

/**
 * Result for a research
 */
class SearchResult
@JvmOverloads
constructor(
        /**
         * Short title
         */
        val title: String,
        /**
         * Description linked to the item being found
         */
        val description: String,
        /**
         * API access point
         */
        val uri: URI,
        /**
         * Web access point
         */
        val page: URI,
        /**
         * Score for the search
         */
        val accuracy: Double,
        /**
         * Type of result
         */
        val type: SearchResultType,
        /**
         * Meta-data which can be used internally
         */
        @get:JsonIgnore
        val data: Map<String, *>? = null
) {
    companion object {
        const val SEARCH_RESULT_ITEM = "item"
    }
}

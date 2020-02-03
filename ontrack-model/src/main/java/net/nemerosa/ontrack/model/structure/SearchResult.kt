package net.nemerosa.ontrack.model.structure

import java.net.URI

/**
 * Result for a research
 */
class SearchResult(
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
        val type: SearchResultType
)

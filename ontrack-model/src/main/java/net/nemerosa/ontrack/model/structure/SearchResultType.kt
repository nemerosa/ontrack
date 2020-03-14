package net.nemerosa.ontrack.model.structure

import net.nemerosa.ontrack.model.extension.ExtensionFeatureDescription

/**
 * Type of [search result][SearchResult.type].
 */
data class SearchResultType(
        /**
         * Associated feature
         */
        val feature: ExtensionFeatureDescription,
        /**
         * ID for the type of search result
         */
        val id: String,
        /**
         * Display name for the search result
         */
        val name: String,
        /**
         * Short help text explaining the format of the token.
         */
        val description: String
)
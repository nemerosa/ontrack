package net.nemerosa.ontrack.model.structure

import net.nemerosa.ontrack.model.extension.ExtensionFeatureDescription

/**
 * Type of [SearchResult].
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
    val name: String
)
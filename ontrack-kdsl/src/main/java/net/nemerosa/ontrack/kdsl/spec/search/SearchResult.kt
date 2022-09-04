package net.nemerosa.ontrack.kdsl.spec.search

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
     * Score for the search
     */
    val accuracy: Double,
    /**
     * Type of result
     */
    val type: SearchResultType,
)

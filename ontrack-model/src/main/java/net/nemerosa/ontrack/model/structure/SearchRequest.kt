package net.nemerosa.ontrack.model.structure

/**
 * Search request.
 *
 * @param token Free text for the search
 * @param type Type of search, linked to [SearchResultType.id]
 */
class SearchRequest @JvmOverloads constructor(
        val token: String,
        val type: String? = null
)

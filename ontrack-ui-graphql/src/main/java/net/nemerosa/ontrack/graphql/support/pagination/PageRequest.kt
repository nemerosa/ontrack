package net.nemerosa.ontrack.graphql.support.pagination

/**
 * Asking for a page in a collection.
 */
class PageRequest(
        val offset: Int = 0,
        val size: Int = DEFAULT_PAGE_SIZE
) {
    companion object {
        const val DEFAULT_PAGE_SIZE = 20
    }
}

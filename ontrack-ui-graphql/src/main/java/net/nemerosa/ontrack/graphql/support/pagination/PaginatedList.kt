package net.nemerosa.ontrack.graphql.support.pagination

/**
 * List of objects with some pagination information.
 *
 * @property pageInfo Information about the current page
 * @property pageItems Items in the current page
 */
class PaginatedList<T>(
        val pageInfo: PageInfo,
        val pageItems: List<T>
)

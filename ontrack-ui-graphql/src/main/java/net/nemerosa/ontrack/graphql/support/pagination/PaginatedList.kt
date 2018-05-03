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
) {
    companion object {
        fun <T> create(
                items: List<T>,
                offset: Int,
                total: Int): PaginatedList<T> {
            return PaginatedList(
                    pageInfo = PageInfo(
                            totalSize = total,
                            currentOffset = offset,
                            currentSize = items.size,
                            previousPage = PageRequest(offset, items.size).previous(total),
                            nextPage = PageRequest(offset, items.size).next(total)
                    ),
                    pageItems = items
            )
        }
    }
}
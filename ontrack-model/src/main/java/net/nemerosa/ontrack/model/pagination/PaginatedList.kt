package net.nemerosa.ontrack.model.pagination

/**
 * List of objects with some pagination information.
 *
 * @property pageInfo Information about the current page
 * @property pageItems Items in the current page
 */
class PaginatedList<T>(
    val pageInfo: PageInfo,
    val pageItems: List<T>,
) {

    fun <S> map(fn: (T) -> S) = PaginatedList<S>(
        pageInfo = pageInfo,
        pageItems = pageItems.map(fn)
    )

    companion object {

        fun <T> ofOne(item: T) = PaginatedList(
            pageInfo = PageInfo(
                totalSize = 1,
                currentOffset = 0,
                currentSize = 1,
                previousPage = null,
                nextPage = null,
            ),
            pageItems = listOf(item)
        )

        fun <T> empty() = PaginatedList(
            pageInfo = PageInfo(
                totalSize = 0,
                currentOffset = 0,
                currentSize = 0,
                previousPage = null,
                nextPage = null,
            ),
            pageItems = emptyList<T>()
        )

        @JvmStatic
        fun <T> create(
            items: List<T>,
            offset: Int,
            pageSize: Int,
        ) = create(
            items.subList(
                maxOf(offset, 0),
                maxOf(minOf(offset + pageSize, items.size), 0)
            ),
            offset,
            pageSize,
            items.size
        )

        fun <T> create(
            items: List<T>,
            offset: Int,
            pageSize: Int,
            total: Int,
        ): PaginatedList<T> {
            return PaginatedList(
                pageInfo = PageInfo(
                    totalSize = total,
                    currentOffset = offset,
                    currentSize = items.size,
                    previousPage = PageRequest(offset, items.size).previous(total, pageSize),
                    nextPage = PageRequest(offset, items.size).next(total, pageSize)
                ),
                pageItems = items
            )
        }
    }
}
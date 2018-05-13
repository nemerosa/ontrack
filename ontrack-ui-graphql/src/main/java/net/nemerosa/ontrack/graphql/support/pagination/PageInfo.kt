package net.nemerosa.ontrack.graphql.support.pagination

import org.apache.commons.lang3.Validate

/**
 * Information on the current page in a [PaginatedList].
 *
 * @property totalSize Total known size of the list
 * @property currentOffset Offset of this page in the collection
 * @property currentSize Size of this page
 * @property previousPage Handle to a previous page if any
 * @property nextPage Handle to a next page if any
 */
class PageInfo(
        val totalSize: Int,
        val currentOffset: Int,
        val currentSize: Int,
        val previousPage: PageRequest?,
        val nextPage: PageRequest?
) {
    init {
        Validate.isTrue(currentOffset >= 0, "Current offset must be >= 0")
        Validate.isTrue(currentSize >= 0, "Current page size must be >= 0")
    }

    /**
     * Index of the page in the total number of pages (starting from 0)
     */
    val pageIndex: Int
        get() = if (currentSize != 0) currentOffset / currentSize else 0

    /**
     * Total number of pages
     */
    val pageTotal: Int
        get() = if (currentSize != 0) (totalSize + currentSize - 1) / currentSize else 0
}



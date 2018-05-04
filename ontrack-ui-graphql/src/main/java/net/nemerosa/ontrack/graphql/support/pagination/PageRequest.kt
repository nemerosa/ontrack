package net.nemerosa.ontrack.graphql.support.pagination

import java.lang.Integer.max
import java.lang.Integer.min

/**
 * Asking for a page in a collection.
 */
class PageRequest(
        val offset: Int = 0,
        val size: Int = DEFAULT_PAGE_SIZE
) {
    /**
     * Computes the previous page request.
     *
     * @param total Total number of elements in the list
     * @param pageSize Size for the previous page
     * @return `null` if there is no previous page
     */
    fun previous(total: Int, pageSize: Int): PageRequest? {
        val newOffset = max(offset - pageSize, 0)
        val newEnd = minOf(offset, total - 1)
        val newSize = newEnd - newOffset
        return if (newSize > 0)
            PageRequest(newOffset, newSize)
        else
            null
    }

    /**
     * Computes the next page request.
     *
     * @param total Total number of elements in the list
     * @param pageSize Size for the next page
     * @return `null` if there is no next page
     */
    fun next(total: Int, pageSize: Int): PageRequest? {
        val newOffset = min(offset + size, total)
        val newEnd = minOf(offset + size + pageSize, total)
        val newSize = newEnd - newOffset
        return if (newSize > 0)
            PageRequest(newOffset, newSize)
        else
            null
    }

    companion object {
        const val DEFAULT_PAGE_SIZE = 20
    }
}

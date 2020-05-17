package net.nemerosa.ontrack.ui.resource

import net.nemerosa.ontrack.model.support.Page
import java.net.URI

data class Pagination(
        val offset: Int,
        val limit: Int,
        val total: Int,
        val prev: URI? = null,
        val next: URI? = null
) {

    fun withPrev(uri: URI?) =
            Pagination(
                    offset,
                    limit,
                    total,
                    uri,
                    next
            )

    fun withNext(uri: URI?) =
            Pagination(
                    offset,
                    limit,
                    total,
                    prev,
                    uri
            )

    companion object {

        @JvmField
        val NONE: Pagination? = null

        @JvmStatic
        fun of(offset: Int, limit: Int, total: Int): Pagination {
            return Pagination(
                    offset,
                    limit,
                    total,
                    null,
                    null
            )
        }

        @JvmStatic
        fun <T> paginate(
                items: List<T>,
                page: Page,
                uriSupplier: (Int, Int) -> URI
        ): PaginatedList<T> {
            val offset = page.offset
            val limit = page.count
            val total = items.size
            val paginatedList = items.subList(offset, minOf(offset + limit, total))
            val actualCount = paginatedList.size
            var pagination = Pagination(offset, actualCount, total)
            // Previous page
            if (offset > 0) {
                pagination = pagination.withPrev(
                        uriSupplier(
                                maxOf(0, offset - limit),
                                limit
                        )
                )
            }
            // Next page
            if (offset + limit < total) {
                pagination = pagination.withNext(
                        uriSupplier(
                                offset + limit,
                                limit
                        )
                )
            }
            // OK
            return PaginatedList(
                    paginatedList,
                    pagination
            )
        }
    }

}

data class PaginatedList<out T>(
        val items: List<T>,
        val pagination: Pagination
)

package net.nemerosa.ontrack.ui.resource

import lombok.experimental.Wither
import net.nemerosa.ontrack.model.support.Page
import java.net.URI

data class Pagination(
        val offset: Int,
        val limit: Int,
        val total: Int,
        @Wither
        val prev: URI? = null,
        @Wither
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
            val actualCount = minOf(limit, total)
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
                    items.subList(offset, minOf(offset + limit, total)),
                    pagination
            )
        }
    }

}

data class PaginatedList<out T>(
        val items: List<T>,
        val pagination: Pagination
)

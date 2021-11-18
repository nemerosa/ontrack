package net.nemerosa.ontrack.kdsl.connector.graphql

import net.nemerosa.ontrack.kdsl.connector.graphql.schema.fragment.PageInfoContent
import net.nemerosa.ontrack.kdsl.connector.support.PaginatedList
import net.nemerosa.ontrack.kdsl.connector.support.emptyPaginatedList

fun <T : Any, R> T?.checkData(
    code: (T) -> R,
) = if (this != null) {
    val r = code(this)
    r ?: throw GraphQLClientException("No data node was returned")
} else {
    throw GraphQLClientException("No data was returned")
}

fun <T : Any, R> T.paginate(
    pageInfo: (T) -> PageInfoContent?,
    pageItems: (T) -> List<R>?,
): PaginatedList<R> = if (pageInfo == null || pageItems == null) {
    emptyPaginatedList()
} else {
    val items = pageItems(this)
    if (items == null) {
        emptyPaginatedList()
    } else {
        PaginatedList(
            items = items,
        )
    }
}

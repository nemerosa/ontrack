package net.nemerosa.ontrack.kdsl.connector.support

fun <T> emptyPaginatedList(): PaginatedList<T> = PaginatedList(
    items = emptyList(),
)

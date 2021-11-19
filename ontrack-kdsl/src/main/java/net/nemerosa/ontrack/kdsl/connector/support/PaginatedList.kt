package net.nemerosa.ontrack.kdsl.connector.support

class PaginatedList<T>(
    val items: List<T>,
) {

    fun <R> map(mapping: (T) -> R): PaginatedList<R> = PaginatedList(
        items = items.map(mapping)
    )

}


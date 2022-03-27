package net.nemerosa.ontrack.model.pagination

typealias Seed<T> = (offset: Int, size: Int) -> Pair<Int, List<T>>

/**
 * Computes a paginated list over several collections in turn.
 *
 * @param T Type of item in the lists
 * @param offset Overall offset
 * @param size Size for a page
 */
fun <T> spanningPaginatedList(
    offset: Int,
    size: Int,
    seeds: Collection<Seed<T>>,
): PaginatedList<T> {
    // Total count collected so far
    var total = 0
    // Sliding offset
    var slidingOffset = offset
    // Total list
    val result = mutableListOf<T>()
    // For each seed
    seeds.forEach { seed ->
        // While the list size does not exceed the page size
        if (slidingOffset >= 0 && result.size < size) {
            // How much do we need to collect still?
            val leftOver = size - result.size
            // Sliding the offset
            slidingOffset = maxOf(0, slidingOffset - total)

            // Total count and items for THIS seed
            val (count, items) = seed(slidingOffset, leftOver)
            // Completing the total
            total += count
            // Completing the collection
            result += items
        }
    }
    // Getting the final page
    return PaginatedList.create(result, offset, size, total)

}
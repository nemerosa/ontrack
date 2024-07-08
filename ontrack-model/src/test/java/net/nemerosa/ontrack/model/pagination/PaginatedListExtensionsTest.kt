package net.nemerosa.ontrack.model.pagination

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class PaginatedListExtensionsTest {

    @Test
    fun `Spanning pagination with one seed`() {
        val items = (1..3).map { "Item $it" }
        val seed: (offset: Int, size: Int) -> Pair<Int, List<String>> = { offset, size: Int ->
            3 to items.drop(offset).take(size)
        }
        val pl = spanningPaginatedList(
            offset = 0,
            size = 2,
            seeds = listOf(seed)
        )
        assertEquals(3, pl.pageInfo.totalSize)
        assertNotNull(pl.pageInfo.nextPage)
        assertEquals(
            listOf(
                "Item 1",
                "Item 2"
            ),
            pl.pageItems
        )
    }

}
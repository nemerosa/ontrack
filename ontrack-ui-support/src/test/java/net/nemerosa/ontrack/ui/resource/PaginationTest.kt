package net.nemerosa.ontrack.ui.resource

import net.nemerosa.ontrack.model.support.Page
import org.junit.Test
import java.net.URI
import kotlin.test.assertEquals
import kotlin.test.assertNull

class PaginationTest {

    private val list = (1..32).toList()

    private val uriSupplier = { offset: Int, count: Int -> URI("uri:page:$offset:$count") }

    @Test
    fun `First page`() {
        val (items, pagination) = Pagination.paginate(
                list,
                Page(0, 10),
                uriSupplier
        )
        assertEquals(
                (1..10).toList(),
                items
        )
        assertEquals(0, pagination.offset)
        assertEquals(10, pagination.limit)
        assertEquals(32, pagination.total)
        assertNull(pagination.prev)
        assertEquals(URI("uri:page:10:10"), pagination.next)
    }

    @Test
    fun `Second page`() {
        val (items, pagination) = Pagination.paginate(
                list,
                Page(10, 10),
                uriSupplier
        )
        assertEquals(
                (11..20).toList(),
                items
        )
        assertEquals(10, pagination.offset)
        assertEquals(10, pagination.limit)
        assertEquals(32, pagination.total)
        assertEquals(URI("uri:page:0:10"), pagination.prev)
        assertEquals(URI("uri:page:20:10"), pagination.next)
    }

    @Test
    fun `Third page`() {
        val (items, pagination) = Pagination.paginate(
                list,
                Page(20, 10),
                uriSupplier
        )
        assertEquals(
                (21..30).toList(),
                items
        )
        assertEquals(20, pagination.offset)
        assertEquals(10, pagination.limit)
        assertEquals(32, pagination.total)
        assertEquals(URI("uri:page:10:10"), pagination.prev)
        assertEquals(URI("uri:page:30:10"), pagination.next)
    }

    @Test
    fun `Last page`() {
        val (items, pagination) = Pagination.paginate(
                list,
                Page(30, 10),
                uriSupplier
        )
        assertEquals(
                (31..32).toList(),
                items
        )
        assertEquals(30, pagination.offset)
        assertEquals(2, pagination.limit)
        assertEquals(32, pagination.total)
        assertEquals(URI("uri:page:20:10"), pagination.prev)
        assertNull(pagination.next)
    }

    @Test
    fun `Page size too big with offset 0`() {
        val (items, pagination) = Pagination.paginate(
                list,
                Page(0, 40),
                uriSupplier
        )
        assertEquals(
                (1..32).toList(),
                items
        )
        assertEquals(0, pagination.offset)
        assertEquals(32, pagination.limit)
        assertEquals(32, pagination.total)
        assertNull(pagination.prev)
        assertNull(pagination.next)
    }

    @Test
    fun `Page size too big with offset gt 0`() {
        val (items, pagination) = Pagination.paginate(
                list,
                Page(10, 40),
                uriSupplier
        )
        assertEquals(
                (11..32).toList(),
                items
        )
        assertEquals(10, pagination.offset)
        assertEquals(22, pagination.limit)
        assertEquals(32, pagination.total)
        assertEquals(URI("uri:page:0:40"), pagination.prev)
        assertNull(pagination.next)
    }

}
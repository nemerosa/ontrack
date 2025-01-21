package net.nemerosa.ontrack.common

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class CollectionUtilsTest {

    @Test
    fun `Moving item from middle to start`() {
        val items = (0..4).toList()
        val reordered = moveItem(items, 2, 0)
        assertEquals(
            listOf(2, 0, 1, 3, 4),
            reordered
        )
    }

    @Test
    fun `Moving item from middle to after start`() {
        val items = (0..4).toList()
        val reordered = moveItem(items, 2, 1)
        assertEquals(
            listOf(0, 2, 1, 3, 4),
            reordered
        )
    }

    @Test
    fun `Moving item from middle to end`() {
        val items = (0..4).toList()
        val reordered = moveItem(items, 2, 4)
        assertEquals(
            listOf(0, 1, 3, 4, 2),
            reordered
        )
    }

    @Test
    fun `Moving item from middle to before end`() {
        val items = (0..4).toList()
        val reordered = moveItem(items, 2, 3)
        assertEquals(
            listOf(0, 1, 3, 2, 4),
            reordered
        )
    }

}
package net.nemerosa.ontrack.extension.indicators.support

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class PercentageTest {

    @Test
    fun boundaries() {
        assertFailsWith<IllegalStateException> { (-1).percent() }
        assertEquals(0, 0.percent().value)
        assertEquals(100, 100.percent().value)
        assertFailsWith<IllegalStateException> { 101.percent() }
    }

    @Test
    fun equality() {
        assertEquals(50.percent(), 50.percent())
    }

    @Test
    fun string() {
        assertEquals("50%", 50.percent().toString())
    }

    @Test
    fun invert() {
        assertEquals(0, 100.percent().invert().value)
        assertEquals(40, 60.percent().invert().value)
        assertEquals(50, 50.percent().invert().value)
        assertEquals(60, 40.percent().invert().value)
        assertEquals(100, 0.percent().invert().value)
    }

    @Test
    fun comparisons() {
        assertEquals(50.percent(), 50.percent())
        assertTrue(51.percent() > 50.percent())
        assertTrue(50.percent() < 51.percent())
    }

}
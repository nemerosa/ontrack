package net.nemerosa.ontrack.extension.indicators.support

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class IntegerThresholdsTest {

    @Test
    fun `Range check`() {
        assertFailsWith<IllegalStateException> { IntegerThresholds(min = -1, max = 0) }
        assertFailsWith<IllegalStateException> { IntegerThresholds(min = 50, max = 40) }
        assertFailsWith<IllegalStateException> { IntegerThresholds(min = 50, max = 50) }
        IntegerThresholds(min = 0, max = 50)
    }

    @Test
    fun `Higher is better`() {
        check(min = 0, max = 50, higherIsBetter = true, value = 0, expected = 0)
        check(min = 0, max = 50, higherIsBetter = true, value = 10, expected = 20)
        check(min = 0, max = 50, higherIsBetter = true, value = 40, expected = 80)
        check(min = 0, max = 50, higherIsBetter = true, value = 50, expected = 100)
        check(min = 0, max = 50, higherIsBetter = true, value = 60, expected = 100)

        check(min = 10, max = 50, higherIsBetter = true, value = 0, expected = 0)
        check(min = 10, max = 50, higherIsBetter = true, value = 10, expected = 0)
        check(min = 10, max = 50, higherIsBetter = true, value = 40, expected = 75)
        check(min = 10, max = 50, higherIsBetter = true, value = 50, expected = 100)
        check(min = 10, max = 50, higherIsBetter = true, value = 60, expected = 100)

        check(min = 40, max = 50, higherIsBetter = true, value = 0, expected = 0)
        check(min = 40, max = 50, higherIsBetter = true, value = 10, expected = 0)
        check(min = 40, max = 50, higherIsBetter = true, value = 40, expected = 0)
        check(min = 40, max = 50, higherIsBetter = true, value = 45, expected = 50)
        check(min = 40, max = 50, higherIsBetter = true, value = 50, expected = 100)
        check(min = 40, max = 50, higherIsBetter = true, value = 60, expected = 100)
    }

    @Test
    fun `Lower is better`() {
        check(min = 0, max = 50, higherIsBetter = false, value = 0, expected = 100)
        check(min = 0, max = 50, higherIsBetter = false, value = 10, expected = 80)
        check(min = 0, max = 50, higherIsBetter = false, value = 40, expected = 20)
        check(min = 0, max = 50, higherIsBetter = false, value = 50, expected = 0)
        check(min = 0, max = 50, higherIsBetter = false, value = 60, expected = 0)

        check(min = 10, max = 50, higherIsBetter = false, value = 0, expected = 100)
        check(min = 10, max = 50, higherIsBetter = false, value = 10, expected = 100)
        check(min = 10, max = 50, higherIsBetter = false, value = 40, expected = 25)
        check(min = 10, max = 50, higherIsBetter = false, value = 50, expected = 0)
        check(min = 10, max = 50, higherIsBetter = false, value = 60, expected = 0)

        check(min = 40, max = 50, higherIsBetter = false, value = 0, expected = 100)
        check(min = 40, max = 50, higherIsBetter = false, value = 10, expected = 100)
        check(min = 40, max = 50, higherIsBetter = false, value = 40, expected = 100)
        check(min = 40, max = 50, higherIsBetter = false, value = 45, expected = 50)
        check(min = 40, max = 50, higherIsBetter = false, value = 50, expected = 0)
        check(min = 40, max = 50, higherIsBetter = false, value = 60, expected = 0)
    }

    private fun check(
            expected: Int,
            min: Int,
            max: Int,
            higherIsBetter: Boolean,
            value: Int
    ) {
        val t = IntegerThresholds(min, max, higherIsBetter)
        val p = t.getCompliance(value)
        assertEquals(expected, p.value)
    }

}
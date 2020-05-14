package net.nemerosa.ontrack.extension.indicators.support

import org.junit.Test
import kotlin.test.assertEquals

class PercentageThresholdTest {

    @Test
    fun `Higher is better`() {
        check(100, 0, 0)
        check(100, 0, 10)
        check(100, 0, 50)
        check(100, 0, 90)
        check(100, 0, 100)

        check(0, 10, 0)
        check(100, 10, 10)
        check(100, 10, 50)
        check(100, 10, 90)
        check(100, 10, 100)

        check(0, 50, 0)
        check(20,50, 10)
        check(100,50, 50)
        check(100,50, 90)
        check(100,50, 100)

        check(0, 90, 0)
        check(11,90, 10)
        check(55,90, 50)
        check(100,90, 90)
        check(100,90, 100)

        check(0, 100, 0)
        check(10,100, 10)
        check(50,100, 50)
        check(90,100, 90)
        check(100,100, 100)
    }

    @Test
    fun `Lower is better`() {
        check(100, 0, 0, false)
        check(90, 0, 10, false)
        check(50, 0, 50, false)
        check(10, 0, 90, false)
        check(0, 0, 100, false)

        check(100, 10, 0, false)
        check(100, 10, 10, false)
        check(55, 10, 50, false)
        check(11, 10, 90, false)
        check(0, 10, 100, false)

        check(100, 50, 0, false)
        check(100,50, 10, false)
        check(100,50, 50, false)
        check(20,50, 90, false)
        check(0,50, 100, false)

        check(100, 90, 0, false)
        check(100,90, 10, false)
        check(100,90, 50, false)
        check(100,90, 90, false)
        check(0,90, 100, false)

        check(100, 100, 0, false)
        check(100,100, 10, false)
        check(100,100, 50, false)
        check(100,100, 90, false)
        check(100,100, 100, false)
    }

    private fun check(
            expected: Int,
            threshold: Int,
            value: Int,
            highterIsBetter: Boolean = true
    ) {
        assertEquals(expected, PercentageThreshold(threshold.percent(), highterIsBetter).getCompliance(value.percent()).value)
    }

}
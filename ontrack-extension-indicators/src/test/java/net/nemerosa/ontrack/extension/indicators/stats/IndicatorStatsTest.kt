package net.nemerosa.ontrack.extension.indicators.stats

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class IndicatorStatsTest {

    @Test
    fun checks() {
        assertFailsWith<IllegalStateException> {
            stats(
                    total = -1,
                    count = 0
            )
        }
        assertFailsWith<IllegalStateException> {
            stats(
                    total = 0,
                    count = -1
            )
        }
        assertFailsWith<IllegalStateException> {
            stats(
                    total = 1,
                    count = 2
            )
        }
    }

    @Test
    fun percent() {
        assertEquals(
                0,
                stats(
                        total = 0,
                        count = 0
                ).percent
        )
        assertEquals(
                0,
                stats(
                        total = 10,
                        count = 0
                ).percent
        )
        assertEquals(
                20,
                stats(
                        total = 10,
                        count = 2
                ).percent
        )
        assertEquals(
                100,
                stats(
                        total = 10,
                        count = 10
                ).percent
        )
    }

    private fun stats(
            total: Int,
            count: Int
    ) = IndicatorStats(
            total = total,
            count = count,
            min = null,
            avg = null,
            max = null,
            minCount = 0,
            maxCount = 0
    )

}
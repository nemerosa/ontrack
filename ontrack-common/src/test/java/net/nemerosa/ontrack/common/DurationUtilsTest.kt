package net.nemerosa.ontrack.common

import org.junit.Test
import java.time.Duration
import kotlin.test.assertEquals

class DurationUtilsTest {

    @Test
    fun `Format is truncated to the highest unit`() {
        val duration = Duration.parse("P2DT13H56M")
        val format = formatDuration(duration)
        assertEquals("2 days", format)
    }

}
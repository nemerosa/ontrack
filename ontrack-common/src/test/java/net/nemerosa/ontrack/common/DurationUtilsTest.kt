package net.nemerosa.ontrack.common

import org.junit.jupiter.api.Test

import java.time.Duration
import kotlin.test.assertEquals

class DurationUtilsTest {

    @Test
    fun `Parsing of durations`() {
        assertEquals(
            Duration.ofMinutes(30),
            parseDuration("1800")
        )
        assertEquals(
            Duration.ofMinutes(30),
            parseDuration("PT30M")
        )
        assertEquals(
            Duration.ofHours(1),
            parseDuration("PT1H")
        )
        assertEquals(
            Duration.ofSeconds(15),
            parseDuration("15s")
        )
        assertEquals(
            Duration.ofMinutes(15),
            parseDuration("15m")
        )
        assertEquals(
            Duration.ofHours(15),
            parseDuration("15h")
        )
        assertEquals(
            Duration.ofDays(15),
            parseDuration("15d")
        )
        assertEquals(
            Duration.ofDays(15 * 7),
            parseDuration("15w")
        )
        assertEquals(
            null,
            parseDuration("15M")
        )
        assertEquals(
            null,
            parseDuration("15m ")
        )
    }

    @Test
    fun `Format is truncated to the highest unit`() {
        val duration = Duration.parse("P2DT13H56M")
        val format = formatDurationForHumans(duration)
        assertEquals("2 days", format)
    }

}
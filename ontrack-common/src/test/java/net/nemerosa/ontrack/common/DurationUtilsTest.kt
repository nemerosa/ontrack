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
    fun `Formatting of durations`() {
        assertEquals(
            "30m",
            Duration.ofMinutes(30).format(),
        )
        assertEquals(
            "1h",
            Duration.ofHours(1).format(),
        )
        assertEquals(
            "15s",
            Duration.ofSeconds(15).format(),
        )
        assertEquals(
            "15m",
            Duration.ofMinutes(15).format(),
        )
        assertEquals(
            "15h",
            Duration.ofHours(15).format(),
        )
        assertEquals(
            "15d",
            Duration.ofDays(15).format(),
        )
        assertEquals(
            "15w",
            Duration.ofDays(15 * 7).format(),
        )
        assertEquals(
            "0s",
            Duration.ZERO.format(),
        )
        assertEquals(
            "366h",
            Duration.ofDays(15).plusHours(6).format(),
        )
    }

    @Test
    fun `Format is truncated to the highest unit`() {
        val duration = Duration.parse("P2DT13H56M")
        val format = formatDurationForHumans(duration)
        assertEquals("2 days", format)
    }

}
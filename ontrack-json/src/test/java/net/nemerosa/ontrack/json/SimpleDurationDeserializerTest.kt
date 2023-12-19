package net.nemerosa.ontrack.json

import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.temporal.ChronoUnit
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class SimpleDurationDeserializerTest {

    @Test
    fun `Blank returns 0`() {
        assertEquals(
            Duration.ZERO,
            SimpleDurationDeserializer.parse("")
        )
    }

    @Test
    fun `x seconds`() {
        assertEquals(
            Duration.ofSeconds(86400),
            SimpleDurationDeserializer.parse("86400s")
        )
    }

    @Test
    fun `x minutes`() {
        assertEquals(
            Duration.ofMinutes(14),
            SimpleDurationDeserializer.parse("14m")
        )
    }

    @Test
    fun `x hours`() {
        assertEquals(
            Duration.ofHours(14),
            SimpleDurationDeserializer.parse("14h")
        )
    }

    @Test
    fun `x days`() {
        assertEquals(
            Duration.ofDays(14),
            SimpleDurationDeserializer.parse("14d")
        )
    }

    @Test
    fun `x weeks`() {
        assertEquals(
            Duration.ofDays(14),
            SimpleDurationDeserializer.parse("2w")
        )
    }

    @Test
    fun `x months not supported`() {
        assertFailsWith<IllegalStateException> {
            SimpleDurationDeserializer.parse("2M")
        }
    }

    @Test
    fun `x years not supported`() {
        assertFailsWith<IllegalStateException> {
            SimpleDurationDeserializer.parse("2y")
        }
    }

    @Test
    fun `x seconds as default`() {
        assertEquals(
            Duration.ofSeconds(14),
            SimpleDurationDeserializer.parse("14")
        )
    }

    @Test
    fun `No match`() {
        assertFailsWith<IllegalStateException> {
            SimpleDurationDeserializer.parse("mm")
        }
    }

    @Test
    fun `No unit match`() {
        assertFailsWith<IllegalStateException> {
            SimpleDurationDeserializer.parse("14x")
        }
    }

}
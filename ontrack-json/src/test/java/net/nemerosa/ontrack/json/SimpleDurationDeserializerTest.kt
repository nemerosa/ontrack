package net.nemerosa.ontrack.json

import org.junit.jupiter.api.Test
import java.time.Duration
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

internal class SimpleDurationDeserializerTest {

    @Test
    fun `Blank returns 0`() {
        assertEquals(
            Duration.ZERO,
            SimpleDurationDeserializer.parse("")
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
    fun `x days as default`() {
        assertEquals(
            Duration.ofDays(14),
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
            SimpleDurationDeserializer.parse("14m")
        }
    }

}
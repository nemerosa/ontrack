package net.nemerosa.ontrack.model.structure

import net.nemerosa.ontrack.common.Time
import org.junit.Test
import java.time.Duration
import kotlin.test.*

class TokenTest {

    @Test
    fun obfuscation() {
        val token = Token("xxx", Time.now(), Time.now() + Duration.ofDays(14))
        val ob = token.obfuscate()
        assertEquals("", ob.value)
        assertEquals(token.creation, ob.creation)
        assertEquals(token.validUntil, ob.validUntil)
    }

    @Test
    fun `Always valid when valid until is null`() {
        val now = Time.now()
        val token = Token("x", now, null)
        assertTrue(token.isValid(), "Valid now")
        assertTrue(token.isValid(now + Duration.ofDays(3650)), "Valid in 10 years")
    }

    @Test
    fun `Limited validity`() {
        val now = Time.now()
        val token = Token("x", now, now + Duration.ofDays(30))
        assertTrue(token.isValid(), "Valid now")
        assertTrue(token.isValid(now + Duration.ofDays(29)), "Valid in 29 days")
        assertFalse(token.isValid(Time.now() + Duration.ofDays(31)), "Not valid in 31 days")
    }

    @Test
    fun `Token valid with null value`() {
        val token = Token("x", Time.now(), null).validFor(null)
        assertNull(token.validUntil, "Always valid")
    }

    @Test
    fun `Token valid with negative value`() {
        val token = Token("x", Time.now(), null).validFor(Duration.ofDays(-1))
        assertNull(token.validUntil, "Always valid")
    }

    @Test
    fun `Token valid with zero value`() {
        val token = Token("x", Time.now(), null).validFor(Duration.ofDays(0))
        assertNull(token.validUntil, "Always valid")
    }

    @Test
    fun `Token valid with value`() {
        val now = Time.now()
        val token = Token("x", now, null).validFor(Duration.ofDays(1))
        assertNotNull(token.validUntil, "Validity") {
            val duration = Duration.between(now, it)
            assertEquals(1, duration.toDays())
        }
    }

}
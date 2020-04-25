package net.nemerosa.ontrack.model.structure

import net.nemerosa.ontrack.common.Time
import net.nemerosa.ontrack.json.asJson
import org.junit.Test
import java.time.Duration
import java.time.LocalDateTime
import kotlin.test.*

class TokenTest {

    @Test
    fun json() {
        val token = Token(
                "xxx",
                LocalDateTime.of(2019, 4, 25, 18, 22, 0),
                LocalDateTime.of(2019, 5, 25, 18, 22, 0)
        )
        assertFalse(token.valid, "Invalid token, in the past")
        assertEquals(
                mapOf(
                        "value" to "xxx",
                        "creation" to "2019-04-25T18:22:00Z",
                        "validUntil" to "2019-05-25T18:22:00Z",
                        "valid" to false
                ).asJson(),
                token.asJson()
        )
    }

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
package net.nemerosa.ontrack.json

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class JsonPathUtilsTest {

    @Test
    fun `Getting null on blank`() {
        val json = mapOf(
            "name" to "Test"
        ).asJson()
        val node = JsonPathUtils.get(json, "")
        assertNull(node)
    }

    @Test
    fun `Getting the root`() {
        val json = mapOf(
            "name" to "Test"
        ).asJson()
        val node = JsonPathUtils.get(json, ".")
        assertEquals(json, node)
    }

    @Test
    fun `Getting a field`() {
        val json = mapOf(
            "name" to "Test"
        ).asJson()
        val node = JsonPathUtils.get(json, "name")
        assertNotNull(node) {
            assertTrue(it.isTextual)
            assertEquals("Test", it.asText())
        }
    }

    @Test
    fun `Getting a path`() {
        val json = mapOf(
            "person" to mapOf(
                "age" to 24
            )
        ).asJson()
        val node = JsonPathUtils.get(json, "person.age")
        assertNotNull(node) {
            assertTrue(it.isInt)
            assertEquals(24, it.asInt())
        }
    }

}
package net.nemerosa.ontrack.model.templating

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class TemplatingSourceExtensionsTest {

    @Test
    fun `Empty expression`() {
        assertTrue(
            parseTemplatingSourceConfig("").isEmpty(),
            "Config is empty"
        )
    }

    @Test
    fun `One config element`() {
        val config = parseTemplatingSourceConfig("name=some-name")
        assertEquals(
            "some-name",
            config.getString("name"),
        )
    }

    @Test
    fun `Missing value`() {
        assertFailsWith<TemplatingConfigFormatException> {
            parseTemplatingSourceConfig("name=some-name&projects=")
        }
    }

    @Test
    fun `Missing key`() {
        assertFailsWith<TemplatingConfigFormatException> {
            parseTemplatingSourceConfig("name=some-name&=24")
        }
    }

    @Test
    fun `Extra separator at the end`() {
        assertFailsWith<TemplatingConfigFormatException> {
            parseTemplatingSourceConfig("name=some-name&")
        }
    }

    @Test
    fun `Extra separator in the middle`() {
        assertFailsWith<TemplatingConfigFormatException> {
            parseTemplatingSourceConfig("name=some-name&&projects=24&valid=true")
        }
    }

    @Test
    fun `Several config elements`() {
        val config = parseTemplatingSourceConfig("name=some-name&projects=24&valid=true")
        assertEquals("some-name", config.getString("name"))
        assertEquals("24", config.getString("projects"))
        assertEquals("true", config.getString("valid"))
    }

    @Test
    fun `Full parsing with repeated keys`() {
        val config = parseTemplatingSourceConfig("root.field?name=value&sections=one&sections=two".substringAfter("?"))
        assertEquals(
            mapOf(
                "name" to listOf("value"),
                "sections" to listOf("one", "two"),
            ),
            config.params
        )
        assertEquals("value", config.getString("name"))
        assertEquals(listOf("one", "two"), config.getList("sections"))
    }

    @Test
    fun `Full parsing with repeated keys and = signs`() {
        val config = parseTemplatingSourceConfig("root.field?name=value&sections=one=1&sections=two=2".substringAfter("?"))
        assertEquals(
            mapOf(
                "name" to listOf("value"),
                "sections" to listOf("one=1", "two=2"),
            ),
            config.params
        )
        assertEquals("value", config.getString("name"))
        assertEquals(listOf("one=1", "two=2"), config.getList("sections"))
    }

    @Test
    fun `Full parsing with boolean`() {
        val config = parseTemplatingSourceConfig("valid=true&invalid=false&missing=some")
        assertTrue(config.getBoolean("valid"))
        assertTrue(!config.getBoolean("invalid"))
        assertTrue(!config.getBoolean("missing"))
        assertTrue(!config.getBoolean("none"))
    }

}
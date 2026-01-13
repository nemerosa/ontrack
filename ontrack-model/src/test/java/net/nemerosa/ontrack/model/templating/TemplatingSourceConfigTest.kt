package net.nemerosa.ontrack.model.templating

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TemplatingSourceConfigTest {

    @Test
    fun `Parsing to a data class`() {
        val config = TemplatingSourceConfig(
            params = mapOf(
                "name" to listOf("myname"),
                "tags" to listOf("a", "b"),
                "flag" to listOf("true"),
                "count" to listOf("10")
            )
        )

        val result = config.parse<TestConfig>()

        assertEquals("myname", result.name)
        assertEquals(listOf("a", "b"), result.tags)
        assertEquals(true, result.flag)
        assertEquals(10, result.count)
    }

    @Test
    fun `Parsing to a data class with defaults`() {
        val config = TemplatingSourceConfig(
            params = mapOf(
                "name" to listOf("myname")
            )
        )

        val result = config.parse<TestConfigWithDefaults>()

        assertEquals("myname", result.name)
        assertEquals(listOf("default"), result.tags)
        assertEquals(false, result.flag)
        assertEquals(0, result.count)
    }

    @Test
    fun `Parsing a single list with one element into a list`() {
        val config = TemplatingSourceConfig(
            params = mapOf(
                "name" to listOf("myname"),
                "tags" to listOf("a")
            )
        )

        val result = config.parse<TestConfig>()

        assertEquals("myname", result.name)
        assertEquals(listOf("a"), result.tags)
    }

    @Test
    fun `Parsing a single value into a list`() {
        val config = TemplatingSourceConfig(
            params = mapOf(
                "name" to listOf("myname"),
                "tags" to listOf("a")
            )
        )

        val result = config.parse<TestConfig>()

        assertEquals("myname", result.name)
        assertEquals(listOf("a"), result.tags)
    }

    @Test
    fun `Parsing a single value into a list (explicitly testing the Jackson feature)`() {
        val config = TemplatingSourceConfig(
            params = mapOf(
                "name" to listOf("myname"),
                "tags" to listOf("a")
            )
        )
        // In TemplatingSourceConfig.parse, it will map "tags" to "a" (String)
        // Jackson should accept "a" as List<String> due to ACCEPT_SINGLE_VALUE_AS_ARRAY
        val result = config.parse<TestConfig>()
        assertEquals(listOf("a"), result.tags)
    }

    data class TestConfig(
        val name: String,
        val tags: List<String>,
        val flag: Boolean,
        val count: Int,
    )

    data class TestConfigWithDefaults(
        val name: String,
        val tags: List<String> = listOf("default"),
        val flag: Boolean = false,
        val count: Int = 0,
    )
}

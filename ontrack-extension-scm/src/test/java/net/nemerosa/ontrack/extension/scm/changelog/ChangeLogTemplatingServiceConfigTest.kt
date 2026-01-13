package net.nemerosa.ontrack.extension.scm.changelog

import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parse
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ChangeLogTemplatingServiceConfigTest {

    @Test
    fun `Parsing dependencies with comma-separated values`() {
        val json = mapOf(
            "dependencies" to listOf("one,two", "three")
        ).asJson()
        val config = json.parse<ChangeLogTemplatingServiceConfig>()
        assertEquals(
            listOf("one", "two", "three"),
            config.dependencies
        )
    }

    @Test
    fun `Parsing dependencies with single value`() {
        val json = mapOf(
            "dependencies" to listOf("one")
        ).asJson()
        val config = json.parse<ChangeLogTemplatingServiceConfig>()
        assertEquals(
            listOf("one"),
            config.dependencies
        )
    }

    @Test
    fun `Parsing dependencies with mixed values`() {
        val json = mapOf(
            "dependencies" to listOf("one,two", "three", "four, five")
        ).asJson()
        val config = json.parse<ChangeLogTemplatingServiceConfig>()
        assertEquals(
            listOf("one", "two", "three", "four", "five"),
            config.dependencies
        )
    }

}

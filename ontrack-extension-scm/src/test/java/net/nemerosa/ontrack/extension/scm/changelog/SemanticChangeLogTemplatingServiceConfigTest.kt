package net.nemerosa.ontrack.extension.scm.changelog

import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parse
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class SemanticChangeLogTemplatingServiceConfigTest {

    @Test
    fun `Parsing dependencies with comma-separated values`() {
        val json = mapOf(
            "dependencies" to listOf("one,two", "three")
        ).asJson()
        val config = json.parse<SemanticChangeLogTemplatingServiceConfig>()
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
        val config = json.parse<SemanticChangeLogTemplatingServiceConfig>()
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
        val config = json.parse<SemanticChangeLogTemplatingServiceConfig>()
        assertEquals(
            listOf("one", "two", "three", "four", "five"),
            config.dependencies
        )
    }

    @Test
    fun `Parsing other fields`() {
        val json = mapOf(
            "issues" to true,
            "allQualifiers" to true,
            "defaultQualifierFallback" to true,
            "exclude" to listOf("alpha", "beta")
        ).asJson()
        val config = json.parse<SemanticChangeLogTemplatingServiceConfig>()
        assertEquals(true, config.issues)
        assertEquals(true, config.allQualifiers)
        assertEquals(true, config.defaultQualifierFallback)
        assertEquals(listOf("alpha", "beta"), config.exclude)
    }
}

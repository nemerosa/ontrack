package net.nemerosa.ontrack.common

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class SimpleExpandTest {

    @Test
    fun `No pattern`() {
        assertEquals(
            "A string without any value",
            SimpleExpand.expand("A string without any value", emptyMap())
        )
    }

    @Test
    fun `One value`() {
        assertEquals(
            "Template [a-z]+ with one value",
            SimpleExpand.expand("Template {regex} with one value", mapOf("regex" to "[a-z]+"))
        )
    }

    @Test
    fun `Two values`() {
        assertEquals(
            "Template with several values 1 and 2",
            SimpleExpand.expand(
                "Template with several values {one} and {two}", mapOf(
                    "one" to "1",
                    "two" to "2"
                )
            )
        )
    }

    @Test
    fun `Two values and a non compliant pattern`() {
        assertEquals(
            "Template with several values 1 and 2 and a non {Compl-iant} one",
            SimpleExpand.expand(
                "Template with several values {one} and {two} and a non {Compl-iant} one", mapOf(
                    "one" to "1",
                    "two" to "2"
                )
            )
        )
    }

    @Test
    fun `Two values and a compliant pattern`() {
        assertEquals(
            "Template with several values 1 and 2 and a perfectly compliant one",
            SimpleExpand.expand(
                "Template with several values {one} and {two} and a {Compliant} one", mapOf(
                    "one" to "1",
                    "two" to "2",
                    "Compliant" to "perfectly compliant",
                )
            )
        )
    }

    @Test
    fun `Two values and a missing value`() {
        assertEquals(
            "Template with several values 1 and 2 and a missing  one",
            SimpleExpand.expand(
                "Template with several values {one} and {two} and a missing {three} one", mapOf(
                    "one" to "1",
                    "two" to "2"
                )
            )
        )
    }

    @Test
    fun `URL encode filter`() {
        assertEquals(
            "pipeline/release%2F1.23",
            SimpleExpand.expand(
                "pipeline/{ScmBranch|urlencode}",
                mapOf(
                    "ScmBranch" to "release/1.23"
                )
            )
        )
    }

    @Test
    fun `Uppercase filter`() {
        assertEquals(
            "Example: MY PROJECT",
            SimpleExpand.expand(
                "Example: {Project|uppercase}",
                mapOf(
                    "Project" to "My project"
                )
            )
        )
    }

    @Test
    fun `Lowercase filter`() {
        assertEquals(
            "Example: my project",
            SimpleExpand.expand(
                "Example: {Project|lowercase}",
                mapOf(
                    "Project" to "My project"
                )
            )
        )
    }

    @Test
    fun `Unknown filter`() {
        assertFailsWith<IllegalStateException> {
            SimpleExpand.expand(
                "Example: {Project|unknown}",
                mapOf(
                    "Project" to "My project"
                )
            )
        }
    }

}
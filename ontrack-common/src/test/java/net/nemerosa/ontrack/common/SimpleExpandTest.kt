package net.nemerosa.ontrack.common

import org.junit.Test
import kotlin.test.assertEquals

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
            SimpleExpand.expand("Template with several values {one} and {two}", mapOf(
                "one" to "1",
                "two" to "2"
            ))
        )
    }

    @Test
    fun `Two values and a non compliant pattern`() {
        assertEquals(
            "Template with several values 1 and 2 and a non {Compl-iant} one",
            SimpleExpand.expand("Template with several values {one} and {two} and a non {Compl-iant} one", mapOf(
                "one" to "1",
                "two" to "2"
            ))
        )
    }

    @Test
    fun `Two values and a compliant pattern`() {
        assertEquals(
            "Template with several values 1 and 2 and a perfectly compliant one",
            SimpleExpand.expand("Template with several values {one} and {two} and a {Compliant} one", mapOf(
                "one" to "1",
                "two" to "2",
                "Compliant" to "perfectly compliant",
            ))
        )
    }

    @Test
    fun `Two values and a missing value`() {
        assertEquals(
            "Template with several values 1 and 2 and a missing  one",
            SimpleExpand.expand("Template with several values {one} and {two} and a missing {three} one", mapOf(
                "one" to "1",
                "two" to "2"
            ))
        )
    }

}
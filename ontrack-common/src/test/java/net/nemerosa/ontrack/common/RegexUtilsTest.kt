package net.nemerosa.ontrack.common

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class RegexUtilsTest {

    @Test
    fun `Replacement at the end`() {
        val regex = "version = (.*)".toRegex()
        val value = regex.replaceGroup("version = 0.9.0", 1, "1.0.0")
        assertEquals("version = 1.0.0", value)
    }

    @Test
    fun `No replacement`() {
        val regex = "version = (.*)".toRegex()
        val value = regex.replaceGroup("value = 0.9.0", 1, "1.0.0")
        assertEquals("value = 0.9.0", value)
    }

    @Test
    fun `Replacement at the start`() {
        val regex = "(.*) = version".toRegex()
        val value = regex.replaceGroup("property = version", 1, "new-property")
        assertEquals("new-property = version", value)
    }

    @Test
    fun `Replacement in the middle`() {
        val regex = "something (.*) great".toRegex()
        val value = regex.replaceGroup("something is great", 1, "was")
        assertEquals("something was great", value)
    }

}
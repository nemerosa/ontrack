package net.nemerosa.ontrack.common

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class UtilsTest {
    @Test
    fun safeRegexMatch_null() {
        assertFalse(Utils.safeRegexMatch(null, null))
        assertFalse(Utils.safeRegexMatch(null, ""))
        assertFalse(Utils.safeRegexMatch(null, "abc"))
    }

    @Test
    fun safeRegexMatch_empty() {
        assertFalse(Utils.safeRegexMatch("", null))
        assertFalse(Utils.safeRegexMatch("", ""))
        assertFalse(Utils.safeRegexMatch("", "abc"))
    }

    @Test
    fun safeRegexMatch_blank() {
        assertFalse(Utils.safeRegexMatch(" ", null))
        assertFalse(Utils.safeRegexMatch(" ", ""))
        assertFalse(Utils.safeRegexMatch(" ", "abc"))
    }

    @Test
    fun safeRegexMatch_correct() {
        assertFalse(Utils.safeRegexMatch("ab.*", null))
        assertFalse(Utils.safeRegexMatch("ab.*", ""))
        assertTrue(Utils.safeRegexMatch("ab.*", "abc"))
        assertFalse(Utils.safeRegexMatch("ab.*", "acb"))
    }

    @Test
    fun safeRegexMatch_incorrect() {
        assertFalse(Utils.safeRegexMatch("ab.*)", null))
        assertFalse(Utils.safeRegexMatch("ab.*)", ""))
        assertFalse(Utils.safeRegexMatch("ab.*)", "abc"))
        assertFalse(Utils.safeRegexMatch("ab.*)", "acb"))
    }

    @Test
    fun asList_null() {
        assertTrue(Utils.asList(null).isEmpty())
    }

    @Test
    fun asList_blank() {
        assertTrue(Utils.asList("").isEmpty())
    }

    @Test
    fun asList_one() {
        assertEquals(
            mutableListOf("Test"),
            Utils.asList("Test")
        )
    }

    @Test
    fun asList_two() {
        assertEquals(
            mutableListOf("Test", "Second"),
            Utils.asList("Test\nSecond")
        )
    }
}
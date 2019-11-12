package net.nemerosa.ontrack.common

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class VersionTest {

    @Test
    fun none() {
        assertEquals("0.0.0", Version.NONE.toString())
    }

    @Test
    fun validity() {
        assertTrue(Version(1, 1, 1).isValid)
        assertTrue(Version(0, 1, 1).isValid)
        assertTrue(Version(0, 0, 1).isValid)
        assertFalse(Version(0, 0, 0).isValid)
        assertFalse(Version(-1, 1, 1).isValid)
        assertFalse(Version(1, -1, 1).isValid)
        assertFalse(Version(1, 1, -1).isValid)
    }

    @Test
    fun compareOnMajor() {
        assertTrue(
                Version(2, 0, 0) > Version(1, 10, 100)
        )
    }

    @Test
    fun compareOnMinor() {
        assertTrue(
                Version(2, 1, 0) > Version(2, 0, 100)
        )
    }

    @Test
    fun compareOnPatch() {
        assertTrue(
                Version(2, 1, 1) > Version(2, 1, 0)
        )
    }

    @Test
    fun compareOnEquality() {
        assertEquals(Version(2, 1, 1), Version(2, 1, 1))
    }

    @Test
    fun defaultVersion() {
        assertEquals(Version(0, 0, 0), Version())
    }

    @Test
    fun toStringValue() {
        assertEquals("1.2.3", Version(1, 2, 3).toString())
    }

    @Test
    fun null_string() {
        assertNull(Version.parseVersion(null))
    }

    @Test
    fun empty_string() {
        assertNull(Version.parseVersion(""))
    }

    @Test
    fun blank_string() {
        assertNull(Version.parseVersion(" "))
    }

    @Test
    fun wrong_version_format() {
        assertNull(Version.parseVersion("m.0.0"))
    }

    @Test
    fun default_version() {
        assertEquals(Version(), Version.parseVersion("0.0.0"))
    }

    @Test
    fun one_digit() {
        assertEquals(Version(3, 0, 0), Version.parseVersion("3"))
    }

    @Test
    fun two_digits() {
        assertEquals(Version(3, 1, 0), Version.parseVersion("3.1"))
    }

    @Test
    fun three_digits() {
        assertEquals(Version(3, 1, 81), Version.parseVersion("3.1.81"))
    }

    @Test
    fun four_digits() {
        assertEquals(Version(3, 1, 81), Version.parseVersion("3.1.81.101"))
    }

}

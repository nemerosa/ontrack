package net.nemerosa.ontrack.model.ordering

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class VersionUtilsTest {

    @Test
    fun `toVersion returns a version`() {
        val o = VersionUtils.toVersion("1.0")
        assertNotNull(o) {
            assertNotNull(it.version) { v ->
                assertEquals(1, v.major)
                assertEquals(0, v.minor)
                assertEquals(0, v.patch)
            }
        }
    }

    @Test
    fun `toVersion does not return a version`() {
        val o = VersionUtils.toVersion("v1.0")
        assertNull(o)
    }

    @Test
    fun `Get version on a path with no match returns null`() {
        assertNull(VersionUtils.getVersion("release/.*".toRegex(), "release-1.0"))
    }

    @Test
    fun `Get version on a path with one matched group returns a version`() {
        assertNotNull(VersionUtils.getVersion("release/(.*)".toRegex(), "release/1.0")) {
            assertEquals("1.0.0", it.version?.toString())
        }
    }

    @Test
    fun `Get version on a path with one separator returns a version`() {
        assertNotNull(VersionUtils.getVersion("release/.*".toRegex(), "release/1.0")) {
            assertEquals("1.0.0", it.version?.toString())
        }
    }

    @Test
    fun `Get version on a path with one separator but no version`() {
        assertNull(VersionUtils.getVersion("release/.*".toRegex(), "release/v1.0"))
    }

    @Test
    fun `Get version on a matching path with no group and no separator`() {
        assertNull(VersionUtils.getVersion("master".toRegex(), "master"))
    }

}
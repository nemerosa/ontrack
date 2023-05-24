package net.nemerosa.ontrack.extension.git.support

import net.nemerosa.ontrack.extension.scm.support.TagPattern
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TagPatternTest {

    @Test
    fun `Tag name validation - default`() {
        val pattern = TagPattern("*")
        assertEquals("*", pattern.pattern)
        assertTrue(pattern.isValidTagName("any"))
        assertTrue(pattern.isValidTagName("2.0.0"))
    }

    @Test
    fun `Tag name validation - simple pattern`() {
        val pattern = TagPattern("2.0.*")
        assertEquals("2.0.*", pattern.pattern)
        assertFalse(pattern.isValidTagName("any"))
        assertTrue(pattern.isValidTagName("2.0.0"))
        assertTrue(pattern.isValidTagName("2.0.1"))
        assertTrue(pattern.isValidTagName("2.0.12"))
        assertFalse(pattern.isValidTagName("v2.0.12"))
        assertFalse(pattern.isValidTagName("2.1.0"))
    }

    @Test
    fun `Tag name validation - capturing group`() {
        val pattern = TagPattern("ontrack-(2.0.*)")
        assertEquals("ontrack-(2.0.*)", pattern.pattern)
        assertFalse(pattern.isValidTagName("any"))
        assertFalse(pattern.isValidTagName("2.0.0"))
        assertTrue(pattern.isValidTagName("ontrack-2.0.0"))
        assertTrue(pattern.isValidTagName("ontrack-2.0.1"))
        assertTrue(pattern.isValidTagName("ontrack-2.0.12"))
        assertFalse(pattern.isValidTagName("v2.0.12"))
        assertFalse(pattern.isValidTagName("ontrack-2.1.0"))
    }

    @Test
    fun `Build name from tag - default`() {
        val pattern = TagPattern("*")
        assertEquals("any", pattern.getBuildNameFromTagName("any").get())
        assertEquals("2.0.0", pattern.getBuildNameFromTagName("2.0.0").get())
    }

    @Test
    fun `Build name from tag - simple`() {
        val pattern = TagPattern("2.0.*")
        assertFalse(pattern.getBuildNameFromTagName("any").isPresent)
        assertEquals("2.0.0", pattern.getBuildNameFromTagName("2.0.0").get())
        assertEquals("2.0.1", pattern.getBuildNameFromTagName("2.0.1").get())
        assertEquals("2.0.12", pattern.getBuildNameFromTagName("2.0.12").get())
        assertFalse(pattern.getBuildNameFromTagName("v2.0.12").isPresent)
        assertFalse(pattern.getBuildNameFromTagName("2.1.0").isPresent)
    }

    @Test
    fun `Build name from tag - capturing group`() {
        val pattern = TagPattern("ontrack-(2.0.*)")
        assertFalse(pattern.getBuildNameFromTagName("any").isPresent)
        assertFalse(pattern.getBuildNameFromTagName("2.0.0").isPresent)
        assertEquals("2.0.0", pattern.getBuildNameFromTagName("ontrack-2.0.0").get())
        assertEquals("2.0.1", pattern.getBuildNameFromTagName("ontrack-2.0.1").get())
        assertEquals("2.0.12", pattern.getBuildNameFromTagName("ontrack-2.0.12").get())
        assertFalse(pattern.getBuildNameFromTagName("v2.0.12").isPresent)
        assertFalse(pattern.getBuildNameFromTagName("ontrack-2.1.0").isPresent)
    }

    @Test
    fun `Tag name from build - default`() {
        val pattern = TagPattern("*")
        assertEquals("any", pattern.getTagNameFromBuildName("any").get())
        assertEquals("2.0.0", pattern.getTagNameFromBuildName("2.0.0").get())
    }

    @Test
    fun `Tag name from build - simple`() {
        val pattern = TagPattern("2.0.*")
        assertFalse(pattern.getTagNameFromBuildName("any").isPresent)
        assertEquals("2.0.0", pattern.getTagNameFromBuildName("2.0.0").get())
        assertEquals("2.0.1", pattern.getTagNameFromBuildName("2.0.1").get())
        assertEquals("2.0.12", pattern.getTagNameFromBuildName("2.0.12").get())
        assertFalse(pattern.getTagNameFromBuildName("v2.0.12").isPresent)
        assertFalse(pattern.getTagNameFromBuildName("2.1.0").isPresent)
    }

    @Test
    fun `Tag name from build - capturing group`() {
        val pattern = TagPattern("ontrack-(2.0.*)")
        assertFalse(pattern.getTagNameFromBuildName("any").isPresent)
        assertEquals("ontrack-2.0.0", pattern.getTagNameFromBuildName("2.0.0").get())
        assertEquals("ontrack-2.0.1", pattern.getTagNameFromBuildName("2.0.1").get())
        assertEquals("ontrack-2.0.12", pattern.getTagNameFromBuildName("2.0.12").get())
        assertFalse(pattern.getTagNameFromBuildName("2.1.0").isPresent)
    }
}

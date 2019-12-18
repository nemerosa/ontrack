package net.nemerosa.ontrack.extension.general

import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ReleasePropertyTypeTest {

    private val type = ReleasePropertyType(
            GeneralExtensionFeature()
    )

    @Test
    fun `Contains value tests`() {
        assertFalse(type.containsValue(ReleaseProperty("Rel one"), "two"))
        assertTrue(type.containsValue(ReleaseProperty("Rel one"), "one"))
        assertTrue(type.containsValue(ReleaseProperty("Rel one"), "ONE"))
        assertTrue(type.containsValue(ReleaseProperty("1.2.3"), "1.2"))
        // Not testing on the JSON
        assertFalse(type.containsValue(ReleaseProperty("1.2.3"), "name"))
        assertFalse(type.containsValue(ReleaseProperty("1.2.3"), "NAME"))
    }
}
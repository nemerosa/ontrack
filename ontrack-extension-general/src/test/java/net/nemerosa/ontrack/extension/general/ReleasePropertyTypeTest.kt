package net.nemerosa.ontrack.extension.general

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
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

    @Test
    fun getEditionForm_empty() {
        val form = type.getEditionForm(null, null)
        assertNull(form.getField("name").value)
    }

    @Test
    fun getEditionForm_not_empty() {
        val form = type.getEditionForm(null, ReleaseProperty("test"))
        assertEquals("test", form.getField("name").value)
    }
}
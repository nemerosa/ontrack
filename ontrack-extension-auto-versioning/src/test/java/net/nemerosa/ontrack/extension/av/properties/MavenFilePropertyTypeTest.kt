package net.nemerosa.ontrack.extension.av.properties

import net.nemerosa.ontrack.test.TestUtils
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class MavenFilePropertyTypeTest {

    @Test
    fun `Replacement keeps the file structure, including comments`() {
        val initial = TestUtils.resourceString("/properties/maven/pom.xml")
        val expected = TestUtils.resourceString("/properties/maven/pom-expected.xml")
        val type = MavenFilePropertyType()

        // Reading
        assertEquals("4.3.39", type.readProperty(initial, "ontrack-v4.version"), "Initial property")

        // Replacement
        val actual = type.replaceProperty(initial, "ontrack-v4.version", "4.4.10")

        // Gets the new version
        assertEquals("4.4.10", type.readProperty(actual, "ontrack-v4.version"), "New property")

        // Checks that the format is kept
        assertEquals(expected, actual, "Formatting")
    }

}
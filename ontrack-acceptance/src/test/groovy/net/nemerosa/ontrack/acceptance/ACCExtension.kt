package net.nemerosa.ontrack.acceptance

import net.nemerosa.ontrack.acceptance.support.AcceptanceTest
import net.nemerosa.ontrack.acceptance.support.AcceptanceTestSuite
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Tests for extensions.
 */
@AcceptanceTestSuite
@AcceptanceTest(value = *arrayOf(AcceptanceTestContext.EXTENSIONS))
class ACCExtension : AbstractACCDSL() {

    @Test
    fun `Information is accessible`() {
        anonymous().get("info").withNode { info ->
            val displayVersion = info.path("version").path("display").asText()
            assertTrue(displayVersion.isNotBlank())
        }
    }

    @Test
    fun `Version check`() {
        anonymous().get("info").withNode { info ->
            val displayVersion = info.path("version").path("display").asText()
            val expectedVersion = configRule.config.version
            assertEquals(expectedVersion, displayVersion)
        }
    }

    @Test
    fun `Extension loaded`() {
        admin().get("extensions").withNode { extensionList ->
            val extensionFeatureDescription = extensionList.path("extensions").find {
                "test" == it.path("id").asText()
            }
            assertNotNull(extensionFeatureDescription) {
                assertEquals("Test", it.path("name").asText())
                assertEquals("Test extension", it.path("description").asText())
            }
        }
    }

    @Test
    fun `Setting a property defined by the extension`() {
        // Creates a project
        val projectName = uid("P")
        ontrack.project(projectName)
        // Gets the test property
        val propertyType = "net.nemerosa.ontrack.extension.test.TestPropertyType"
        val property = ontrack.project(projectName).config.property(propertyType, false)
        assertNull(property, "Property is not set yet")
        // Sets the property
        val value = uid("V")
        ontrack.project(projectName).config.property(
                propertyType,
                mapOf("value" to value)
        )
        // Gets the property again
        @Suppress("UNCHECKED_CAST")
        val map = ontrack.project(projectName).config.property(propertyType, false) as Map<String, String>
        assertNotNull(map)
        assertEquals(value, map["value"])
    }

    @Test
    fun third_party_library_test() {
        // Just call a REST end point which relies on 3rd party dependency
        anonymous().get("extension/test/3rdparty?value=2&power=3").withNode { json ->
            val result = json.path("result").asInt()
            assertEquals(8, result.toLong())
        }
    }

}
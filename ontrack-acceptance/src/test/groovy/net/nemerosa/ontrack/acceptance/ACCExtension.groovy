package net.nemerosa.ontrack.acceptance

import net.nemerosa.ontrack.acceptance.support.AcceptanceTest
import net.nemerosa.ontrack.acceptance.support.AcceptanceTestSuite
import org.apache.commons.lang3.StringUtils
import org.junit.Test

import static net.nemerosa.ontrack.test.TestUtils.uid

/**
 * Tests for extensions.
 */
@AcceptanceTestSuite
@AcceptanceTest(value = [AcceptanceTestContext.EXTENSIONS], explicit = true)
class ACCExtension extends AbstractACCDSL {

    @Test
    void 'Information is accessible'() {
        anonymous().get("info").withNode { info ->
            def displayVersion = info.path("version").path("display").asText()
            assert (StringUtils.isNotBlank(displayVersion))
        }
    }

    @Test
    void 'Extension loaded'() {
        admin().get("extensions").withNode { extensionList ->
            def extensionFeatureDescription = extensionList.path("extensions").find {
                "test" == it.path("id").asText()
            }
            assert (extensionFeatureDescription != null)
            assert "Test" == extensionFeatureDescription.path("name").asText()
            assert "Test extension" == extensionFeatureDescription.path("description").asText()
        }
    }

    @Test
    void 'Setting a property defined by the extension'() {
        // Creates a project
        def projectName = uid("P")
        ontrack.project(projectName)
        // Gets the test property
        def propertyType = "net.nemerosa.ontrack.extension.test.TestPropertyType"
        def property = ontrack.project(projectName).config.property(propertyType, false)
        assert (property == null): "Property is not set yet"
        // Sets the property
        def value = uid("V")
        ontrack.project(projectName).config.property(
                propertyType,
                ["value": value]
        )
        // Gets the property again
        def map = ontrack.project(projectName).config.property(propertyType, false) as Map<String, String>
        assert (map != null)
        assert (value == map.value)
    }

    @Test
    void 'Third party library test'() {
        // Just call a REST end point which relies on 3rd party dependency
        anonymous().get("extension/test/3rdparty?value=2&power=3").withNode { json ->
            def result = json.path("result").asInt()
            assert (8 == result)
        }
    }

}
package net.nemerosa.ontrack.graphql

import net.nemerosa.ontrack.extension.api.support.TestConfiguration
import net.nemerosa.ontrack.extension.api.support.TestConfigurationService
import net.nemerosa.ontrack.extension.api.support.TestProperty
import net.nemerosa.ontrack.extension.api.support.TestPropertyType
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class ConfigurationPropertyGraphQLIT : AbstractQLKTITSupport() {

    @Autowired
    private lateinit var testConfigurationService: TestConfigurationService

    @Test
    fun `Obfuscation for configuration password`() {
        // Creates a test configuration containing a password
        val configName = uid("C")
        val configuration = TestConfiguration(
                configName,
                "user",
                "secret"
        )
        asAdmin().execute {
            testConfigurationService.newConfiguration(
                    configuration
            )
        }
        // Checks the password is still accessible programmatically
        asAdmin().execute {
            val c = testConfigurationService.getConfiguration(configName)
            assertEquals("secret", c.password, "Password must remain accessible programmatically")
        }
        // Creates a project
        val project = doCreateProject()
        // Configures the property on the project
        asAdmin().execute {
            propertyService.editProperty(
                    project,
                    TestPropertyType::class.java,
                    TestProperty(
                            configuration,
                            "Test"
                    )
            )
        }
        // Gets the property for the project using GraphQL
        val data = asAdmin().call {
            run("""
{
    projects(id: ${project.id}) {
       testProperty {
          value
       }
    }
}""")
        }
        // Gets the value
        val value = data["projects"][0]["testProperty"]["value"]
        assertEquals("Test", value["value"].asText())
        assertEquals(configName, value["configuration"]["name"].asText())
        assertEquals("user", value["configuration"]["user"].asText())
        assertFalse(value["configuration"].has("password"), "Password field must have been removed")
    }

}
package net.nemerosa.ontrack.extension.sonarqube.casc

import net.nemerosa.ontrack.extension.casc.AbstractCascTestSupport
import net.nemerosa.ontrack.extension.sonarqube.configuration.SonarQubeConfiguration
import net.nemerosa.ontrack.extension.sonarqube.configuration.SonarQubeConfigurationService
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parseAsJson
import net.nemerosa.ontrack.test.TestUtils
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class SonarQubeConfigurationCascIT : AbstractCascTestSupport() {

    @Autowired
    private lateinit var sonarQubeConfigurationService: SonarQubeConfigurationService

    @Autowired
    private lateinit var sonarQubeConfigurationCasc: SonarQubeConfigurationCasc

    @Test
    fun `CasC schema type`() {
        val type = sonarQubeConfigurationCasc.jsonType
        assertEquals(
            """
                {
                  "items": {
                    "title": "SonarQubeConfigurationCascData",
                    "description": null,
                    "properties": {
                      "name": {
                        "description": "Name of the configuration",
                        "type": "string"
                      },
                      "password": {
                        "description": "Token for the authentication",
                        "type": "string"
                      },
                      "url": {
                        "description": "URL to SonarQube",
                        "type": "string"
                      }
                    },
                    "required": [
                      "name",
                      "url"
                    ],
                    "additionalProperties": false,
                    "type": "object"
                  },
                  "description": "List of SonarQube configurations",
                  "type": "array"
                }
            """.trimIndent().parseAsJson(),
            type.asJson()
        )
    }

    @Test
    fun `Defining a SonarQube configuration`() {
        val name = TestUtils.uid("sq_")
        withDisabledConfigurationTest {
            casc(
                """
                    ontrack:
                        config:
                            sonarqube:
                                - name: $name
                                  url: https://sonarqube.nemerosa.com
                                  password: my-secret-token
                """.trimIndent()
            )
        }
        // Checks the SonarQube configuration has been registered
        asAdmin {
            val configurations = sonarQubeConfigurationService.configurations
            assertEquals(1, configurations.size, "Only one configuration is defined")
            val configuration = configurations.first()
            assertEquals(name, configuration.name)
            assertEquals("https://sonarqube.nemerosa.com", configuration.url)
            assertEquals("my-secret-token", configuration.password)
        }
    }

    @Test
    fun `Rendering the SonarQube configuration`() {
        val name = TestUtils.uid("sq_")
        asAdmin {
            // Deleting all previous configuration
            sonarQubeConfigurationService.configurations.forEach { configuration ->
                sonarQubeConfigurationService.deleteConfiguration(configuration.name)
            }
            // Creating a new configuration
            val configuration = SonarQubeConfiguration(
                name = name,
                url = "https://$name.nemerosa.com",
                password = "very-secret-token",
            )
            withDisabledConfigurationTest {
                sonarQubeConfigurationService.newConfiguration(configuration)
            }
            // Rendering the Casc
            val node = sonarQubeConfigurationCasc.render()
            assertEquals(
                listOf(
                    mapOf(
                        "name" to name,
                        "url" to "https://$name.nemerosa.com",
                        "password" to "",
                    )
                ).asJson(),
                node,
            )
        }
    }

}
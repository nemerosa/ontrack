package net.nemerosa.ontrack.extension.jira.casc

import net.nemerosa.ontrack.extension.casc.AbstractCascTestSupport
import net.nemerosa.ontrack.extension.jira.JIRAConfigurationService
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parseAsJson
import net.nemerosa.ontrack.model.json.schema.JsonTypeBuilder
import net.nemerosa.ontrack.test.TestUtils
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class JIRAConfigurationCascIT : AbstractCascTestSupport() {

    @Autowired
    private lateinit var jiraConfigurationService: JIRAConfigurationService

    @Autowired
    private lateinit var jiraConfigurationCasc: JIRAConfigurationCasc

    @Autowired
    private lateinit var jsonTypeBuilder: JsonTypeBuilder

    @Test
    fun `CasC schema type`() {
        val type = jiraConfigurationCasc.jsonType(jsonTypeBuilder)
        assertEquals(
            """
                {
                  "items": {
                    "title": "JIRAConfiguration",
                    "description": null,
                    "properties": {
                      "exclude": {
                        "items": {
                          "description": "exclude field",
                          "type": "string"
                        },
                        "description": "exclude field",
                        "type": "array"
                      },
                      "include": {
                        "items": {
                          "description": "include field",
                          "type": "string"
                        },
                        "description": "include field",
                        "type": "array"
                      },
                      "url": {
                        "description": "url field",
                        "type": "string"
                      },
                      "name": {
                        "description": "name field",
                        "type": "string"
                      },
                      "password": {
                        "description": "password field",
                        "type": "string"
                      },
                      "user": {
                        "description": "user field",
                        "type": "string"
                      }
                    },
                    "required": [
                      "url",
                      "name"
                    ],
                    "additionalProperties": false,
                    "type": "object"
                  },
                  "description": "List of JIRA configurations",
                  "type": "array"
                }
            """.trimIndent().parseAsJson(),
            type.asJson()
        )
    }

    @Test
    fun `Defining a JIRA configuration`() {
        val name = TestUtils.uid("J")
        withDisabledConfigurationTest {
            casc(
                """
                    ontrack:
                        config:
                            jira:
                                - name: $name
                                  url: https://jira.nemerosa.com
                                  user: my-user
                                  password: my-secret-token
                """.trimIndent()
            )
        }
        // Checks the JIRA configuration has been registered
        asAdmin {
            val configurations = jiraConfigurationService.configurations
            assertEquals(1, configurations.size, "Only one configuration is defined")
            val configuration = configurations.first()
            assertEquals(name, configuration.name)
            assertEquals("https://jira.nemerosa.com", configuration.url)
            assertEquals("my-user", configuration.user)
            assertEquals("my-secret-token", configuration.password)
        }
    }

    @Test
    fun `Reloading a JIRA configuration`() {
        asAdmin {
            val name = TestUtils.uid("J")
            withDisabledConfigurationTest {
                casc(
                    """
                        ontrack:
                            config:
                                jira:
                                    - name: $name
                                      url: https://jira.nemerosa.com
                                      user: my-user
                                      password: my-secret-token
                    """.trimIndent()
                )
                assertNotNull(jiraConfigurationService.findConfiguration(name)) {
                    assertEquals("my-user", it.user)
                    assertEquals("my-secret-token", it.password)
                }

                // Reloading
                casc(
                    """
                        ontrack:
                            config:
                                jira:
                                    - name: $name
                                      url: https://jira.nemerosa.com
                                      user: my-other-user
                                      password: my-super-secret-token
                    """.trimIndent()
                )
                assertNotNull(jiraConfigurationService.findConfiguration(name)) {
                    assertEquals("my-other-user", it.user)
                    assertEquals("my-super-secret-token", it.password)
                }
            }
        }
    }

}
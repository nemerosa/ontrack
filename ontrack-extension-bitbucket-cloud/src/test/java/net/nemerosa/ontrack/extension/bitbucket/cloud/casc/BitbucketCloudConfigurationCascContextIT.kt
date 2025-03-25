package net.nemerosa.ontrack.extension.bitbucket.cloud.casc

import net.nemerosa.ontrack.extension.bitbucket.cloud.bitbucketCloudTestConfigMock
import net.nemerosa.ontrack.extension.bitbucket.cloud.configuration.BitbucketCloudConfigurationService
import net.nemerosa.ontrack.extension.casc.AbstractCascTestSupport
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parseAsJson
import net.nemerosa.ontrack.model.json.schema.JsonTypeBuilder
import net.nemerosa.ontrack.test.TestUtils.uid
import net.nemerosa.ontrack.test.assertNotPresent
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class BitbucketCloudConfigurationCascContextIT : AbstractCascTestSupport() {

    @Autowired
    private lateinit var bitbucketCloudConfigurationService: BitbucketCloudConfigurationService

    @Autowired
    private lateinit var bitbucketCloudConfigurationCascContext: BitbucketCloudConfigurationCascContext

    @Autowired
    private lateinit var jsonTypeBuilder: JsonTypeBuilder

    @Test
    fun `CasC schema type`() {
        val type = bitbucketCloudConfigurationCascContext.jsonType(jsonTypeBuilder)
        assertEquals(
            """
                {
                  "items": {
                    "title": "BitbucketCloudConfigurationCascData",
                    "description": null,
                    "properties": {
                      "name": {
                        "description": "Name of the configuration",
                        "type": "string"
                      },
                      "password": {
                        "description": "App password used to connect to Bitbucket Cloud",
                        "type": "string"
                      },
                      "user": {
                        "description": "Name of the user used to connect to Bitbucket Cloud",
                        "type": "string"
                      },
                      "workspace": {
                        "description": "Slug of the Bitbucket Cloud workspace to connect to",
                        "type": "string"
                      }
                    },
                    "required": [
                      "name",
                      "user",
                      "workspace"
                    ],
                    "additionalProperties": false,
                    "type": "object"
                  },
                  "description": "List of Bitbucket Cloud configurations",
                  "type": "array"
                }
            """.trimIndent().parseAsJson(),
            type.asJson()
        )
    }

    @BeforeEach
    fun init() {
        asAdmin {
            val names = bitbucketCloudConfigurationService.configurations.map { it.name }
            names.forEach { name ->
                bitbucketCloudConfigurationService.deleteConfiguration(name)
            }
        }
    }

    @Test
    fun `Bitbucket Cloud configuration CasC`() {
        val config = bitbucketCloudTestConfigMock()
        withDisabledConfigurationTest {
            casc(
                """
                    ontrack:
                        config:
                            bitbucket-cloud:
                                - name: ${config.name}
                                  workspace: ${config.workspace}
                                  user: ${config.user}
                                  password: ${config.password}
                """.trimIndent()
            )
            asAdmin {
                val savedConfig = bitbucketCloudConfigurationService.getConfiguration(config.name)
                assertEquals(config.workspace, savedConfig.workspace)
                assertEquals(config.user, savedConfig.user)
                assertEquals(config.password, savedConfig.password)
            }
        }
    }

    @Test
    fun `Bitbucket Cloud configuration CasC - updating a configuration`() {
        withDisabledConfigurationTest {
            val config = bitbucketCloudTestConfigMock()
            asAdmin {
                bitbucketCloudConfigurationService.newConfiguration(config)
            }
            val newWorkspace = uid("w")
            casc(
                """
                    ontrack:
                        config:
                            bitbucket-cloud:
                                - name: ${config.name}
                                  workspace: $newWorkspace
                                  user: ${config.user}
                                  password: ${config.password}
                """.trimIndent()
            )
            asAdmin {
                val savedConfig = bitbucketCloudConfigurationService.getConfiguration(config.name)
                assertEquals(newWorkspace, savedConfig.workspace)
                assertEquals(config.user, savedConfig.user)
                assertEquals(config.password, savedConfig.password)
            }
        }
    }

    @Test
    fun `Bitbucket Cloud configuration CasC - removing and adding a configuration`() {
        withDisabledConfigurationTest {
            val config1 = bitbucketCloudTestConfigMock(workspace = uid("w"))
            asAdmin {
                bitbucketCloudConfigurationService.newConfiguration(config1)
            }
            val config2 = bitbucketCloudTestConfigMock(workspace = uid("w"))
            casc(
                """
                    ontrack:
                        config:
                            bitbucket-cloud:
                                - name: ${config2.name}
                                  workspace: ${config2.workspace}
                                  user: ${config2.user}
                                  password: ${config2.password}
                """.trimIndent()
            )
            asAdmin {
                val oldConfig = bitbucketCloudConfigurationService.getOptionalConfiguration(config1.name)
                assertNotPresent(oldConfig, "Old config has been removed")
                val savedConfig = bitbucketCloudConfigurationService.getConfiguration(config2.name)
                assertEquals(config2.workspace, savedConfig.workspace)
                assertEquals(config2.user, savedConfig.user)
                assertEquals(config2.password, savedConfig.password)
            }
        }
    }

}
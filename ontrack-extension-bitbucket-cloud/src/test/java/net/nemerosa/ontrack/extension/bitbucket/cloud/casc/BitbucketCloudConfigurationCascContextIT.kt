package net.nemerosa.ontrack.extension.bitbucket.cloud.casc

import net.nemerosa.ontrack.extension.bitbucket.cloud.bitbucketCloudTestConfigMock
import net.nemerosa.ontrack.extension.bitbucket.cloud.configuration.BitbucketCloudConfigurationService
import net.nemerosa.ontrack.extension.casc.AbstractCascTestSupport
import net.nemerosa.ontrack.test.TestUtils.uid
import net.nemerosa.ontrack.test.assertNotPresent
import org.junit.Before
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class BitbucketCloudConfigurationCascContextIT : AbstractCascTestSupport() {

    @Autowired
    private lateinit var bitbucketCloudConfigurationService: BitbucketCloudConfigurationService

    @Before
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
                assertEquals(config.workspace, newWorkspace)
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
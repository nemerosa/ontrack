package net.nemerosa.ontrack.extension.bitbucket.cloud.configuration

import net.nemerosa.ontrack.extension.bitbucket.cloud.TestOnBitbucketCloud
import net.nemerosa.ontrack.extension.bitbucket.cloud.bitbucketCloudTestConfigReal
import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.support.ConnectionResult
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class BitbucketCloudConfigurationServiceIT : AbstractDSLTestSupport() {

    @Autowired
    private lateinit var bitbucketCloudConfigurationService: BitbucketCloudConfigurationService

    @Test
    fun `Creation of a configuration`() {
        withDisabledConfigurationTest {
            asAdmin {
                val name = uid("C")
                val workspace = uid("W")
                val config = BitbucketCloudConfiguration(
                    name = name,
                    workspace = workspace,
                    user = "user",
                    password = "xxxx"
                )
                val savedConfig = bitbucketCloudConfigurationService.newConfiguration(config)
                assertEquals(name, savedConfig.name)
                assertEquals(workspace, savedConfig.workspace)
                assertEquals("user", savedConfig.user)
                assertEquals("", savedConfig.password)

                val list = bitbucketCloudConfigurationService.configurations
                assertNotNull(
                    list.find { it.name == name },
                    "Created configuration is in the list"
                )
            }
        }
    }

    @Test
    fun `Updating a configuration`() {
        withDisabledConfigurationTest {
            asAdmin {
                val name = uid("C")
                val workspace = uid("W")
                val config = BitbucketCloudConfiguration(
                    name = name,
                    workspace = workspace,
                    user = "user",
                    password = "xxxx"
                )
                bitbucketCloudConfigurationService.newConfiguration(config)

                // Update
                val newWorkspace = uid("W")
                bitbucketCloudConfigurationService.updateConfiguration(
                    name,
                    BitbucketCloudConfiguration(
                        name = name,
                        workspace = newWorkspace,
                        user = "user",
                        password = "xxxx"
                    )
                )

                val savedConfig: BitbucketCloudConfiguration = bitbucketCloudConfigurationService.getConfiguration(name)
                assertEquals(name, savedConfig.name)
                assertEquals(newWorkspace, savedConfig.workspace)
                assertEquals("user", savedConfig.user)
                assertEquals("xxxx", savedConfig.password)
            }
        }
    }

    @Test
    fun `Deleting a configuration`() {
        withDisabledConfigurationTest {
            asAdmin {
                val name = uid("C")
                val workspace = uid("W")
                val config = BitbucketCloudConfiguration(
                    name = name,
                    workspace = workspace,
                    user = "user",
                    password = "xxxx"
                )
                bitbucketCloudConfigurationService.newConfiguration(config)

                bitbucketCloudConfigurationService.deleteConfiguration(name)

                val list = bitbucketCloudConfigurationService.configurations
                assertNull(
                    list.find { it.name == name },
                    "Deleted configuration cannot be found any longer"
                )
            }
        }
    }

    @TestOnBitbucketCloud
    fun `Testing a configuration`() {
        asAdmin {
            val config = bitbucketCloudTestConfigReal()
            val result = bitbucketCloudConfigurationService.test(config)
            assertEquals(ConnectionResult.ConnectionResultType.OK, result.type)
        }
    }

}
package net.nemerosa.ontrack.extension.bitbucket.cloud.property

import net.nemerosa.ontrack.extension.bitbucket.cloud.AbstractBitbucketCloudTestSupport
import net.nemerosa.ontrack.extension.bitbucket.cloud.bitbucketCloudTestConfigMock
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class BitbucketCloudProjectConfigurationPropertyTypeIT : AbstractBitbucketCloudTestSupport() {

    @Test
    fun `Setting the property on a project`() {
        withDisabledConfigurationTest {
            asAdmin {
                project {
                    val config = bitbucketCloudTestConfigMock()
                    bitbucketCloudConfigurationService.newConfiguration(config)
                    setBitbucketCloudProperty(
                        config,
                        repository = "my-repository",
                        indexationInterval = 30,
                        issueServiceConfigurationIdentifier = "jira//my-jira",
                    )
                    // Gets the property back
                    assertNotNull(
                        propertyService.getProperty(
                            this,
                            BitbucketCloudProjectConfigurationPropertyType::class.java
                        ).value
                    ) {
                        assertEquals(config.name, it.configuration.name)
                        assertEquals("my-repository", it.repository)
                        assertEquals(30, it.indexationInterval)
                        assertEquals("jira//my-jira", it.issueServiceConfigurationIdentifier)
                    }
                }
            }
        }
    }

    @Test
    fun `Cleanup of the project property when configuration is deleted`() {
        withDisabledConfigurationTest {
            asAdmin {
                project {
                    val config = withBitbucketCloudProperty()
                    // Checks the property is set
                    assertTrue(propertyService.hasProperty(this, BitbucketCloudProjectConfigurationPropertyType::class.java), "Property is set")
                    // Deletes the configuration
                    bitbucketCloudConfigurationService.deleteConfiguration(config.name)
                    // Checks the property's gone
                    assertFalse(propertyService.hasProperty(this, BitbucketCloudProjectConfigurationPropertyType::class.java), "Property is unset")
                }
            }
        }
    }

}
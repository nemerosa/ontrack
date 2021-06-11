package net.nemerosa.ontrack.extension.bitbucket.cloud.property

import net.nemerosa.ontrack.extension.bitbucket.cloud.AbstractBitbucketCloudTestSupport
import net.nemerosa.ontrack.extension.bitbucket.cloud.bitbucketCloudTestConfigMock
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

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

}
package net.nemerosa.ontrack.extension.bitbucket.cloud.property

import net.nemerosa.ontrack.extension.bitbucket.cloud.bitbucketCloudTestConfigMock
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class BitbucketCloudProjectConfigurationPropertyTest {

    @Test
    fun `Repository URL`() {
        val config = bitbucketCloudTestConfigMock(workspace = "my-workspace")
        val property = BitbucketCloudProjectConfigurationProperty(
            configuration = config,
            repository = "my-repository",
            indexationInterval = 0,
            issueServiceConfigurationIdentifier = null,
        )
        assertEquals("https://bitbucket.org/my-workspace/my-repository", property.repositoryUrl)
    }

}
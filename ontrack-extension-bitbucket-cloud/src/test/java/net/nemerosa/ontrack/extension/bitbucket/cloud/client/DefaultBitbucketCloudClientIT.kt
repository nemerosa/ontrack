package net.nemerosa.ontrack.extension.bitbucket.cloud.client

import net.nemerosa.ontrack.extension.bitbucket.cloud.bitbucketCloudTestEnv
import org.junit.Before
import org.junit.Test
import kotlin.test.assertNotNull

class DefaultBitbucketCloudClientIT {

    private lateinit var client: BitbucketCloudClient

    @Before
    fun init() {
        val env = bitbucketCloudTestEnv
        client = DefaultBitbucketCloudClient(
            workspace = env.workspace,
            user = env.user,
            token = env.token,
        )
    }

    @Test
    fun `Getting the list of projects`() {
        val expectedProject = bitbucketCloudTestEnv.expectedProject
        val projects = client.projects
        assertNotNull(
            projects.find { it.key == expectedProject },
            "Expecting $expectedProject in the list of projects"
        )
    }

}
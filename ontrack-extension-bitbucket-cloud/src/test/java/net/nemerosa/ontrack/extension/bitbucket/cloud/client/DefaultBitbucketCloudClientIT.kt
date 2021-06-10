package net.nemerosa.ontrack.extension.bitbucket.cloud.client

import net.nemerosa.ontrack.test.getEnv
import org.junit.Before
import org.junit.Test
import kotlin.test.assertNotNull

class DefaultBitbucketCloudClientIT {

    private lateinit var client: BitbucketCloudClient

    @Before
    fun init() {
        client = DefaultBitbucketCloudClient(
            workspace = getEnv("ontrack.test.extension.bitbucket.cloud.workspace"),
            user = getEnv("ontrack.test.extension.bitbucket.cloud.user"),
            token = getEnv("ontrack.test.extension.bitbucket.cloud.token"),
        )
    }

    @Test
    fun `Getting the list of projects`() {
        val expectedProject = getEnv("ontrack.test.extension.bitbucket.cloud.expected.project")
        val projects = client.projects
        assertNotNull(
            projects.find { it.key == expectedProject },
            "Expecting $expectedProject in the list of projects"
        )
    }

}
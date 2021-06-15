package net.nemerosa.ontrack.extension.bitbucket.cloud

import net.nemerosa.ontrack.extension.bitbucket.cloud.configuration.BitbucketCloudConfiguration
import net.nemerosa.ontrack.extension.git.GitExtensionFeature
import net.nemerosa.ontrack.extension.scm.SCMExtensionFeature
import net.nemerosa.ontrack.extension.stale.StaleExtensionFeature
import net.nemerosa.ontrack.test.TestUtils.uid
import net.nemerosa.ontrack.test.getEnv

class BitbucketCloudTestEnv(
    val workspace: String,
    val user: String,
    val token: String,
    val expectedProject: String,
    val expectedRepository: String,
)

val bitbucketCloudExtensionFeature: BitbucketCloudExtensionFeature by lazy {
    BitbucketCloudExtensionFeature(GitExtensionFeature(SCMExtensionFeature(), StaleExtensionFeature()))
}

val bitbucketCloudTestEnv: BitbucketCloudTestEnv by lazy {
    BitbucketCloudTestEnv(
        workspace = getEnv("ontrack.test.extension.bitbucket.cloud.workspace"),
        user = getEnv("ontrack.test.extension.bitbucket.cloud.user"),
        token = getEnv("ontrack.test.extension.bitbucket.cloud.token"),
        expectedProject = getEnv("ontrack.test.extension.bitbucket.cloud.expected.project"),
        expectedRepository = getEnv("ontrack.test.extension.bitbucket.cloud.expected.repository"),
    )
}

/**
 * Creates a real configuration for Bitbucket Cloud, suitable for system tests.
 */
fun bitbucketCloudTestConfigReal(name: String = uid("C")) = bitbucketCloudTestEnv.run {
    BitbucketCloudConfiguration(
        name = name,
        workspace = workspace,
        user = user,
        password = token,
    )
}


fun bitbucketCloudTestConfigMock(
    name: String = uid("C"),
    workspace: String = "my-workspace",
) =
    BitbucketCloudConfiguration(
        name = name,
        workspace = workspace,
        user = "user",
        password = "token",
    )

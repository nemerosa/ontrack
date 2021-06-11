package net.nemerosa.ontrack.extension.bitbucket.cloud

import net.nemerosa.ontrack.extension.bitbucket.cloud.configuration.BitbucketCloudConfiguration
import net.nemerosa.ontrack.test.TestUtils.uid
import net.nemerosa.ontrack.test.getEnv

class BitbucketCloudTestEnv(
    val workspace: String,
    val user: String,
    val token: String,
    val expectedProject: String,
)

val bitbucketCloudTestEnv: BitbucketCloudTestEnv by lazy {
    BitbucketCloudTestEnv(
        workspace = getEnv("ontrack.test.extension.bitbucket.cloud.workspace"),
        user = getEnv("ontrack.test.extension.bitbucket.cloud.user"),
        token = getEnv("ontrack.test.extension.bitbucket.cloud.token"),
        expectedProject = getEnv("ontrack.test.extension.bitbucket.cloud.expected.project"),
    )
}

fun bitbucketCloudTestConfig(name: String = uid("C")) = bitbucketCloudTestEnv.run {
    BitbucketCloudConfiguration(
        name = name,
        workspace = workspace,
        user = user,
        password = token,
    )
}

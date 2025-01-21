package net.nemerosa.ontrack.extension.bitbucket.cloud

import net.nemerosa.ontrack.extension.bitbucket.cloud.configuration.BitbucketCloudConfiguration
import net.nemerosa.ontrack.test.TestUtils.uid
import net.nemerosa.ontrack.test.getEnv
import net.nemerosa.ontrack.test.getOptionalEnv
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIf

class BitbucketCloudTestEnv(
    val ignore: Boolean,
    val workspace: String,
    val user: String,
    val token: String,
    val expectedProject: String,
    val expectedRepository: String,
)

val bitbucketCloudTestEnv: BitbucketCloudTestEnv by lazy {
    BitbucketCloudTestEnv(
        ignore = getOptionalEnv("ontrack.test.extension.bitbucket.cloud.ignore")?.let { it == "true" } ?: false,
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

/**
 * Annotation to use on tests relying on an external Bitbucket Cloud repository.
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@Test
@EnabledIf("net.nemerosa.ontrack.extension.bitbucket.cloud.TestOnBitbucketCloudCondition#isTestOnBitbucketCloudEnabled")
annotation class TestOnBitbucketCloud

/**
 * Testing if the environment is set for testing against Bitbucket Cloud
 */
class TestOnBitbucketCloudCondition {

    companion object {
        @JvmStatic
        fun isTestOnBitbucketCloudEnabled(): Boolean =
            getOptionalEnv("ontrack.test.extension.bitbucket.cloud.ignore") == "true" ||
                    !getOptionalEnv("ontrack.test.extension.bitbucket.cloud.workspace").isNullOrBlank()
    }
}

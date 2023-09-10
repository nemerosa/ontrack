package net.nemerosa.ontrack.extension.stash

import net.nemerosa.ontrack.test.getOptionalEnv
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIf

data class BitbucketServerEnv(
    val enabled: Boolean,
    val url: String,
    val username: String,
    val password: String,
    val project: String,
    val repository: String,
    val defaultBranch: String,
    val autoMergeUser: String?,
    val autoMergeToken: String?,
    val path: String,
    val pathContent: String,
)

val bitbucketServerEnv: BitbucketServerEnv by lazy {
    BitbucketServerEnv(
        enabled = getOptionalEnv("ontrack.test.extension.stash.enabled") == "true",
        url = getOptionalEnv("ontrack.test.extension.stash.url") ?: "http://localhost:7990",
        username = getOptionalEnv("ontrack.test.extension.stash.username") ?: "admin",
        password = getOptionalEnv("ontrack.test.extension.stash.password") ?: "admin",
        project = getOptionalEnv("ontrack.test.extension.stash.project") ?: "TEST",
        repository = getOptionalEnv("ontrack.test.extension.stash.repository") ?: "test",
        defaultBranch = getOptionalEnv("ontrack.test.extension.stash.branch") ?: "main",
        autoMergeUser = getOptionalEnv("ontrack.test.extension.stash.autoMergeUser") ?: "auto_merge",
        autoMergeToken = getOptionalEnv("ontrack.test.extension.stash.autoMergeToken"),
        path = getOptionalEnv("ontrack.test.extension.stash.path") ?: "test.txt",
        pathContent = getOptionalEnv("ontrack.test.extension.stash.pathContent") ?: "This is a test",
    )
}

/**
 * Annotation to use on tests relying on an local Bitbucket Server.
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@Test
@EnabledIf("net.nemerosa.ontrack.extension.stash.TestOnBitbucketServerCondition#isTestOnBitbucketServerEnabled")
annotation class TestOnBitbucketServer

/**
 * Testing if the environment is set for testing against GitHub.
 *
 * Used by [TestOnBitbucketServer].
 */
@Suppress("unused")
class TestOnBitbucketServerCondition {

    companion object {
        @JvmStatic
        fun isTestOnBitbucketServerEnabled(): Boolean =
            getOptionalEnv("ontrack.test.extension.stash.enabled") == "true"
    }
}
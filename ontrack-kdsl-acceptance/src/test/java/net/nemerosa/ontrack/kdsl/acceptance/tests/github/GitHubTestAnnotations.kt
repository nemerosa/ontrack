package net.nemerosa.ontrack.kdsl.acceptance.tests.github

import net.nemerosa.ontrack.kdsl.acceptance.tests.support.getOptionalEnv
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIf


/**
 * Testing if the environment is set for testing against the GitHub playground.
 *
 * Used by [TestOnGitHubPlayground].
 */
@Suppress("unused")
class TestOnGitHubPlaygroundCondition {

    companion object {
        @JvmStatic
        fun isTestOnGitHubPlaygroundEnabled(): Boolean =
            !getOptionalEnv("ontrack.test.extension.github.playground.organization").isNullOrBlank()
    }

}

/**
 * Annotation to use on tests relying on an external GitHub playground.
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@Test
@EnabledIf("net.nemerosa.ontrack.kdsl.acceptance.tests.github.TestOnGitHubPlaygroundCondition#isTestOnGitHubPlaygroundEnabled")
annotation class TestOnGitHubPlayground

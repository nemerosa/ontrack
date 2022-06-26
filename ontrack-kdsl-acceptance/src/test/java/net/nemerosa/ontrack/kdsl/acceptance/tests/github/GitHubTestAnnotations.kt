package net.nemerosa.ontrack.kdsl.acceptance.tests.github

import net.nemerosa.ontrack.kdsl.acceptance.tests.ACCProperties
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIf


/**
 * Annotation to use on tests relying on GitHub.
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@Test
@EnabledIf("net.nemerosa.ontrack.kdsl.acceptance.tests.github.TestOnGitHubCondition#isTestOnGitHubEnabled")
annotation class TestOnGitHub

/**
 * Annotation to use on tests relying on GitHub postprocessing.
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@Test
@TestOnGitHub
@EnabledIf("net.nemerosa.ontrack.kdsl.acceptance.tests.github.TestOnGitHubPostProcessingCondition#isTestOnGitHubPostProcessingEnabled")
annotation class TestOnGitHubPostProcessing

/**
 * Testing if the environment is set for testing against GitHub.
 *
 * Used by [TestOnGitHub].
 */
@Suppress("unused")
class TestOnGitHubCondition {

    companion object {
        @JvmStatic
        fun isTestOnGitHubEnabled(): Boolean =
            ACCProperties.GitHub.organization != null
    }

}

/**
 * Testing if the environment is set for testing post-processing against GitHub.
 *
 * Used by [TestOnGitHubPostProcessing].
 */
@Suppress("unused")
class TestOnGitHubPostProcessingCondition {

    companion object {
        @JvmStatic
        fun isTestOnGitHubPostProcessingEnabled(): Boolean =
            TestOnGitHubCondition.isTestOnGitHubEnabled() &&
                    ACCProperties.GitHub.AutoVersioning.PostProcessing.Processor.org != null &&
                    ACCProperties.GitHub.AutoVersioning.PostProcessing.Processor.repository != null &&
                    ACCProperties.GitHub.AutoVersioning.PostProcessing.Sample.org != null &&
                    ACCProperties.GitHub.AutoVersioning.PostProcessing.Sample.repository != null &&
                    ACCProperties.GitHub.AutoVersioning.PostProcessing.Sample.version != null
    }

}
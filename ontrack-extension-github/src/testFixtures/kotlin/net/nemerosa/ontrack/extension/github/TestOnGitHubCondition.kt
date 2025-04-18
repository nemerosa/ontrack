package net.nemerosa.ontrack.extension.github

import net.nemerosa.ontrack.test.getOptionalEnv

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
            !getOptionalEnv("ontrack.test.extension.github.organization").isNullOrBlank()
    }
}
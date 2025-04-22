package net.nemerosa.ontrack.kdsl.acceptance.tests.github

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
            !System.getenv("ONTRACK_ACCEPTANCE_GITHUB_ORGANIZATION").isNullOrBlank()
    }
}
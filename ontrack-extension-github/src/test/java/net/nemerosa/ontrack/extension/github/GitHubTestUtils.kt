package net.nemerosa.ontrack.extension.github

import net.nemerosa.ontrack.extension.github.model.GitHubEngineConfiguration
import net.nemerosa.ontrack.test.TestUtils
import net.nemerosa.ontrack.test.getEnv
import net.nemerosa.ontrack.test.getOptionalEnv
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIf

data class GitHubTestEnv(
    val user: String,
    val token: String,
    val organization: String,
    val repository: String,
    val issue: Int,
    val pr: Int,
    val team: String,
    val appId: String,
    val appPrivateKey: String,
    val appInstallationAccountName: String?,
    val branch: String,
    val readme: String,
    val paths: GitHubTestEnvKnownPaths,
) {
    val fullRepository: String = "$organization/$repository"
}

data class GitHubTestEnvKnownPaths(
    val images: GitHubTestEnvKnownImagesPaths,
)

data class GitHubTestEnvKnownImagesPaths(
    val validation: String,
)

/**
 * Actual environment for system tests.
 */
val githubTestEnv: GitHubTestEnv by lazy {
    GitHubTestEnv(
        user = getEnv("ontrack.test.extension.github.user"),
        token = getEnv("ontrack.test.extension.github.token"),
        organization = getEnv("ontrack.test.extension.github.organization"),
        repository = getEnv("ontrack.test.extension.github.repository"),
        issue = getEnv("ontrack.test.extension.github.issue").toInt(),
        pr = getEnv("ontrack.test.extension.github.pr").toInt(),
        team = getEnv("ontrack.test.extension.github.team"),
        appId = getEnv("ontrack.test.extension.github.app.id"),
        appPrivateKey = getEnv("ontrack.test.extension.github.app.pem"),
        appInstallationAccountName = getOptionalEnv("ontrack.test.extension.github.app.installation"),
        branch = getOptionalEnv("ontrack.test.extension.github.branch.name") ?: "main",
        readme = getOptionalEnv("ontrack.test.extension.github.branch.readme") ?: "README.md",
        paths = GitHubTestEnvKnownPaths(
            images = GitHubTestEnvKnownImagesPaths(
                validation = getEnv("ontrack.test.extension.github.paths.images.validation"),
            ),
        ),
    )
}

/**
 * Creates a real configuration for GitHub, suitable for system tests.
 */
fun githubTestConfigReal(name: String = TestUtils.uid("C")) = githubTestEnv.run {
    GitHubEngineConfiguration(
        name = name,
        user = user,
        password = null,
        url = null,
        oauth2Token = token,
    )
}

/**
 * Annotation to use on tests relying on an external GitHub repository.
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@Test
@EnabledIf("net.nemerosa.ontrack.extension.github.TestOnGitHubCondition#isTestOnGitHubEnabled")
annotation class TestOnGitHub

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

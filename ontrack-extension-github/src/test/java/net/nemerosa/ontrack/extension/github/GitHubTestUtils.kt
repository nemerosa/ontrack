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
    val pr: Int,
    val team: String,
    val appId: String,
    val appPrivateKey: String,
    val appInstallationAccountName: String?,
    val branch: String,
    val readme: String,
    val paths: GitHubTestEnvKnownPaths,
    val changeLog: GitHubTestChangeLog,
    val issues: GitHubTestIssues,
) {
    val fullRepository: String = "$organization/$repository"
}

data class GitHubTestEnvKnownPaths(
    val images: GitHubTestEnvKnownImagesPaths,
)

data class GitHubTestEnvKnownImagesPaths(
    val validation: String,
    val promotion: String,
)

data class GitHubTestChangeLog(
    val from: String,
    val to: String,
    val messages: List<String>,
)

data class GitHubTestIssues(
    val from: String,
    val to: String,
    val messages: List<String>,
    val issue: Int,
    val issueSummary: String,
    val issueLabels: List<String>,
    val milestone: String,
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
        pr = getEnv("ontrack.test.extension.github.pr").toInt(),
        team = getEnv("ontrack.test.extension.github.team"),
        appId = getEnv("ontrack.test.extension.github.app.id"),
        appPrivateKey = getEnv("ontrack.test.extension.github.app.pem"),
        appInstallationAccountName = getOptionalEnv("ontrack.test.extension.github.app.installation"),
        branch = getOptionalEnv("ontrack.test.extension.github.branch.name") ?: "v1",
        readme = getOptionalEnv("ontrack.test.extension.github.branch.readme") ?: "README.md",
        paths = GitHubTestEnvKnownPaths(
            images = GitHubTestEnvKnownImagesPaths(
                validation = getEnv("ontrack.test.extension.github.paths.images.validation"),
                promotion = getEnv("ontrack.test.extension.github.paths.images.promotion"),
            ),
        ),
        changeLog = GitHubTestChangeLog(
            from = getEnv("ontrack.test.extension.github.changelog.from"),
            to = getEnv("ontrack.test.extension.github.changelog.to"),
            messages = getEnv("ontrack.test.extension.github.changelog.messages").split("|"),
        ),
        issues = GitHubTestIssues(
            from = getEnv("ontrack.test.extension.github.issues.from"),
            to = getEnv("ontrack.test.extension.github.issues.to"),
            messages = getEnv("ontrack.test.extension.github.issues.messages").split("|"),
            issue = getEnv("ontrack.test.extension.github.issues.issue").toInt(),
            issueSummary = getEnv("ontrack.test.extension.github.issues.issueSummary"),
            issueLabels = getEnv("ontrack.test.extension.github.issues.issueLabels").split("|"),
            milestone = getEnv("ontrack.test.extension.github.issues.milestone"),
        )
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

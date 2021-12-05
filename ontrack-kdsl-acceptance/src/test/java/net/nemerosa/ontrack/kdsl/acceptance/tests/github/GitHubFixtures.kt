package net.nemerosa.ontrack.kdsl.acceptance.tests.github

import net.nemerosa.ontrack.kdsl.acceptance.tests.support.getEnv
import net.nemerosa.ontrack.kdsl.acceptance.tests.support.getOptionalEnv

class GitHubTestEnv(
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
) {
    val fullRepository: String = "$organization/$repository"
}

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
    )
}


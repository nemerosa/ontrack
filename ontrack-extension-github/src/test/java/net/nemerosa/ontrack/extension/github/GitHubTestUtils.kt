package net.nemerosa.ontrack.extension.github

import net.nemerosa.ontrack.extension.github.model.GitHubEngineConfiguration
import net.nemerosa.ontrack.test.TestUtils
import net.nemerosa.ontrack.test.getEnv
import net.nemerosa.ontrack.test.getOptionalEnv

class GitHubTestEnv(
    val user: String,
    val token: String,
    val organization: String,
    val repository: String,
    val issue: Int,
    val team: String,
    val appId: String,
    val appPrivateKey: String,
    val appInstallationAccountName: String?,
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
        team = getEnv("ontrack.test.extension.github.team"),
        appId = getEnv("ontrack.test.extension.github.app.id"),
        appPrivateKey = getEnv("ontrack.test.extension.github.app.pem"),
        appInstallationAccountName = getOptionalEnv("ontrack.test.extension.github.app.installation"),
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

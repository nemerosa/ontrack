package net.nemerosa.ontrack.kdsl.spec.extension.github

/**
 * Configuration for using GitHub in Ontrack.
 */
class GitHubConfiguration(
    val name: String,
    val url: String?,
    val user: String? = null,
    val password: String? = null,
    val oauth2Token: String? = null,
    val appId: String? = null,
    val appPrivateKey: String? = null,
    val appInstallationAccountName: String? = null
)

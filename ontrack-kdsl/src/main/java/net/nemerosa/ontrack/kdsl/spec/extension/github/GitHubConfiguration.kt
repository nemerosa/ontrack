package net.nemerosa.ontrack.kdsl.spec.extension.github

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import net.nemerosa.ontrack.kdsl.spec.Configuration

/**
 * Configuration for using GitHub in Ontrack.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
class GitHubConfiguration(
    override val name: String,
    val url: String?,
    val user: String? = null,
    val password: String? = null,
    val oauth2Token: String? = null,
    val appId: String? = null,
    val appPrivateKey: String? = null,
    val appInstallationAccountName: String? = null,
    val autoMergeToken: String? = null,
) : Configuration

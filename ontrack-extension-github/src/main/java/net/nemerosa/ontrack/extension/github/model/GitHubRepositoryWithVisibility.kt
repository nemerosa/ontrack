package net.nemerosa.ontrack.extension.github.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

/**
 * Raw visibility information about a repository visibility.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
class GitHubRepositoryWithVisibility(
    val visibility: String?,
)

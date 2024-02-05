package net.nemerosa.ontrack.extension.github.client

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class GitHubCommitInfo(
    val author: GitHubAuthor?,
    val committer: GitHubAuthor,
    val message: String,
)

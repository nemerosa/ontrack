package net.nemerosa.ontrack.extension.github.client

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class GitHubCommit(
    val sha: String,
    @JsonProperty("html_url")
    val url: String,
    val commit: GitHubCommitInfo,
    val parents: List<GitHubCommitParentRef>?,
)

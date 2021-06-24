package net.nemerosa.ontrack.extension.github.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class GitHubTeam(
    val slug: String,
    val name: String,
    val description: String?,
    val html_url: String
)

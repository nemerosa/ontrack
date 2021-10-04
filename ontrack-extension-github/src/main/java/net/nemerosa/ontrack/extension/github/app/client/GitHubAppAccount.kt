package net.nemerosa.ontrack.extension.github.app.client

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class GitHubAppAccount(
    val login: String,
)
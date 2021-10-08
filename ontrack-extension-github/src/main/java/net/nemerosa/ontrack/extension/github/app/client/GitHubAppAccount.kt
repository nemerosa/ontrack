package net.nemerosa.ontrack.extension.github.app.client

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class GitHubAppAccount(
    val login: String,
    @JsonProperty("html_url")
    val url: String,
)
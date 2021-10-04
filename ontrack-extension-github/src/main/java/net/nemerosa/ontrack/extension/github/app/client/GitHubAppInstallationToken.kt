package net.nemerosa.ontrack.extension.github.app.client

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

@JsonIgnoreProperties(ignoreUnknown = true)
data class GitHubAppInstallationToken(
    val token: String,
    @JsonProperty("expires_at")
    val expiresAt: Date,
)

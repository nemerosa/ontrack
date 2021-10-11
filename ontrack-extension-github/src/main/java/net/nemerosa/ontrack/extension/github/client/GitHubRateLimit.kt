package net.nemerosa.ontrack.extension.github.client

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class GitHubRateLimit(
    val core: RateLimit,
    val graphql: RateLimit,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class RateLimit(
    val limit: Int,
    val remaining: Int,
    val used: Int,
)
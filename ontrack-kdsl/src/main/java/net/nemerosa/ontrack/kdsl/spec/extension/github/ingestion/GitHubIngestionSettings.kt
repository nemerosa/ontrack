package net.nemerosa.ontrack.kdsl.spec.extension.github.ingestion

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
class GitHubIngestionSettings(
    val token: String,
    val retentionDays: Int,
    val orgProjectPrefix: Boolean,
    val indexationInterval: Int,
    val repositoryIncludes: String,
    val repositoryExcludes: String,
    val issueServiceIdentifier: String,
    val enabled: Boolean,
) {
    fun withToken(token: String) = GitHubIngestionSettings(
        token = token,
        retentionDays = retentionDays,
        orgProjectPrefix = orgProjectPrefix,
        indexationInterval = indexationInterval,
        repositoryIncludes = repositoryIncludes,
        repositoryExcludes = repositoryExcludes,
        issueServiceIdentifier = issueServiceIdentifier,
        enabled = enabled,
    )
}

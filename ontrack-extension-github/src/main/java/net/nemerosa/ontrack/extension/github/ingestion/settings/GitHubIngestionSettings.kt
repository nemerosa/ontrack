package net.nemerosa.ontrack.extension.github.ingestion.settings

import net.nemerosa.ontrack.model.annotations.APIDescription

/**
 * Settings for the ingestion of GitHub workflows.
 *
 * @property token Secret token sent by the GitHub hook and signing the payload
 * @property retentionDays Number of days to keep the received payloads (0 = forever)
 * @property orgProjectPrefix Must the organization name be used as a project name prefix?
 * @property indexationInterval Default indexation interval when configuring the GitHub projects
 */
class GitHubIngestionSettings(
    @APIDescription("Secret token sent by the GitHub hook and signing the payload")
    val token: String,
    @APIDescription("Number of days to keep the received payloads (0 = forever)")
    val retentionDays: Int,
    @APIDescription("Must the organization name be used as a project name prefix?")
    val orgProjectPrefix: Boolean,
    @APIDescription("Default indexation interval when configuring the GitHub projects")
    val indexationInterval: Int,
) {
    companion object {
        /**
         * Keeping the payloads 30 days by default.
         */
        const val DEFAULT_RETENTION_DAYS = 30
        /**
         * Not using the organization as a project's name prefix
         */
        const val DEFAULT_ORG_PROJECT_PREFIX = false
        /**
         * 30 minutes by default for the indexation interval
         */
        const val DEFAULT_INDEXATION_INTERVAL = 30
    }
}

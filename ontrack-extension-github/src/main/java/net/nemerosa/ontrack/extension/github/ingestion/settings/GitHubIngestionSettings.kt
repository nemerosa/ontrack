package net.nemerosa.ontrack.extension.github.ingestion.settings

/**
 * Settings for the ingestion of GitHub workflows.
 *
 * @property token Secret token sent by the GitHub hook and signing the payload
 * @property retentionDays Number of days to keep the received payloads (0 = forever)
 * @property orgProjectPrefix Must the organization name be used as a project name prefix?
 */
class GitHubIngestionSettings(
    val token: String,
    val retentionDays: Int,
    val orgProjectPrefix: Boolean,
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
    }
}

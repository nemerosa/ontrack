package net.nemerosa.ontrack.extension.github.ingestion.settings

/**
 * Settings for the ingestion of GitHub workflows.
 *
 * @property token Secret token sent by the GitHub hook and signing the payload
 * @property retentionDays Number of days to keep the received payloads (0 = forever)
 */
class GitHubIngestionSettings(
    val token: String,
    val retentionDays: Int,
) {
    companion object {
        /**
         * Keeping the payloads 30 days by default.
         */
        const val DEFAULT_RETENTION_DAYS = 30
    }
}

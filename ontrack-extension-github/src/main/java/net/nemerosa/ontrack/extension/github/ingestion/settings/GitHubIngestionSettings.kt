package net.nemerosa.ontrack.extension.github.ingestion.settings

/**
 * Settings for the ingestion of GitHub workflows.
 *
 * @property token Secret token sent by the GitHub hook and signing the payload
 */
class GitHubIngestionSettings(
    val token: String,
)

package net.nemerosa.ontrack.kdsl.spec.extension.github.ingestion

/**
 * Representation of a payload
 */
class GitHubIngestionPayload(
    val uuid: String,
    val status: String,
    val message: String?,
    val routing: String?,
    val queue: String?,
)
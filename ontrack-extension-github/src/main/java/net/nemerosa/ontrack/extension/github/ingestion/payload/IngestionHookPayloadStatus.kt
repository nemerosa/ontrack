package net.nemerosa.ontrack.extension.github.ingestion.payload

/**
 * Processing status for a payload.
 */
enum class IngestionHookPayloadStatus {
    SCHEDULED,
    PROCESSING,
    ERRORED,
    COMPLETED,
}
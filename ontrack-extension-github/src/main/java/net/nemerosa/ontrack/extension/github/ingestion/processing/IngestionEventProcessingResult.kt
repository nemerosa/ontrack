package net.nemerosa.ontrack.extension.github.ingestion.processing

enum class IngestionEventProcessingResult {
    /**
     * Event was processed
     */
    PROCESSED,

    /**
     * Event was ignored
     */
    IGNORED;

    operator fun plus(other: IngestionEventProcessingResult): IngestionEventProcessingResult = when (this) {
        IGNORED -> other
        else -> this
    }
}
package net.nemerosa.ontrack.extension.github.ingestion.processing

/**
 * Result of a pre-processing check.
 */
enum class IngestionEventPreprocessingCheck {

    /**
     * Event to be processed
     */
    TO_BE_PROCESSED,

    /**
     * Event won't be processed
     */
    IGNORED,

}
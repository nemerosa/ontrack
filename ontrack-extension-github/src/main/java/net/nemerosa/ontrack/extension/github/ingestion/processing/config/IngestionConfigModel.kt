package net.nemerosa.ontrack.extension.github.ingestion.processing.config

/**
 * Configuration for the ingestion.
 */
data class IngestionConfig(
    val general: IngestionConfigGeneral,
)

/**
 * General settings
 */
data class IngestionConfigGeneral(
    val skipJobs: Boolean = true,
)

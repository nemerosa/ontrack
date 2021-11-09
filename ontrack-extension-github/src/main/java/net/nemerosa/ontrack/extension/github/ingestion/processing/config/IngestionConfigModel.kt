package net.nemerosa.ontrack.extension.github.ingestion.processing.config

/**
 * Default path for the ingestion file
 */
const val INGESTION_CONFIG_FILE_PATH = ".github/ontrack/ingestion.yaml"

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

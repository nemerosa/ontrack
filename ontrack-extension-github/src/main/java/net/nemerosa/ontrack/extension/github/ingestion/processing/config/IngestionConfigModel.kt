package net.nemerosa.ontrack.extension.github.ingestion.processing.config

/**
 * Default path for the ingestion file
 */
const val INGESTION_CONFIG_FILE_PATH = ".github/ontrack/ingestion.yml"

/**
 * Configuration for the ingestion.
 */
data class IngestionConfig(
    val general: IngestionConfigGeneral = IngestionConfigGeneral(),
)

/**
 * General settings
 */
data class IngestionConfigGeneral(
    val skipJobs: Boolean = true,
    val indexationInterval: Int = 30,
)

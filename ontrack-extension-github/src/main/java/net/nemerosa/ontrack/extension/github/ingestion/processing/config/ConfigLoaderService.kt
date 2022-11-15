package net.nemerosa.ontrack.extension.github.ingestion.processing.config

import net.nemerosa.ontrack.extension.github.ingestion.config.model.IngestionConfig
import net.nemerosa.ontrack.model.structure.Branch

/**
 * Service used to download the ingestion configuration from GitHub.
 */
interface ConfigLoaderService {
    /**
     * Loads the configuration from GitHub.
     *
     * @param branch Ontrack branch
     * @param path Path to the ingestion file
     * @return The parsed configuration or null if the target file cannot be found.
     */
    fun loadConfig(branch: Branch, path: String): IngestionConfig?
}
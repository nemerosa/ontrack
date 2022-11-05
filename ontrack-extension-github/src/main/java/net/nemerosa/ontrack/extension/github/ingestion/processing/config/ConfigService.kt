package net.nemerosa.ontrack.extension.github.ingestion.processing.config

import net.nemerosa.ontrack.extension.github.ingestion.config.model.IngestionConfig
import net.nemerosa.ontrack.model.structure.Branch

/**
 * Default path for the ingestion file
 */
const val INGESTION_CONFIG_FILE_PATH = ".github/ontrack/ingestion.yml"

/**
 * Management of ingestion configurations.
 */
interface ConfigService {

    /**
     * Loads & saves the ingestion configuration for a branch.
     *
     * @param branch Ontrack branch
     * @param path Path to the ingestion file
     * @return Saved configuration (null if it could get loaded)
     */
    fun loadAndSaveConfig(branch: Branch, path: String): IngestionConfig?

    /**
     * Saves a configuration for a branch.
     *
     * @param branch Branch
     * @param config Configuration to save
     */
    fun saveConfig(branch: Branch, config: IngestionConfig)

    /**
     * Removes the ingestion configuration for a repository.
     *
     * @param branch Ontrack branch
     */
    fun removeConfig(branch: Branch)

    /**
     * Loads the existing ingestion configuration for a branch.
     */
    fun findConfig(branch: Branch): IngestionConfig?

    /**
     * Gets the existing configuration for a branch, loads it if necessary.
     *
     * @param branch Ontrack branch
     * @param path Path to the ingestion file
     * @return Saved configuration (never null)
     */
    fun getOrLoadConfig(branch: Branch, path: String): IngestionConfig
}
package net.nemerosa.ontrack.extension.github.ingestion.processing.config

import net.nemerosa.ontrack.extension.github.ingestion.processing.model.Repository

/**
 * Management of ingestion configurations.
 */
interface ConfigService {
    /**
     * Loads & saves the ingestion configuration for a repository.
     *
     * @param repository GitHub repository
     * @param branch Git branch
     * @param path Path to the ingestion file
     * @return Saved configuration (null if it could get loaded)
     */
    fun saveConfig(repository: Repository, branch: String, path: String): IngestionConfig?

    /**
     * Removes the ingestion configuration for a repository.
     *
     * @param repository GitHub repository
     * @param branch Git branch
     */
    fun removeConfig(repository: Repository, branch: String)

    /**
     * Loads the existing ingestion configuration for a repository and a branch.
     */
    fun findConfig(repository: Repository, branch: String): IngestionConfig?

    /**
     * Gets the existing configuration for a repository and a branch, loads it if necessary.
     *
     * @param repository GitHub repository
     * @param branch Git branch
     * @param path Path to the ingestion file
     * @return Saved configuration (never null)
     */
    fun getOrLoadConfig(repository: Repository, branch: String, path: String): IngestionConfig
}
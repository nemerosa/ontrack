package net.nemerosa.ontrack.extension.github.ingestion.config

/**
 * Management of ingestion configurations.
 */
interface ConfigService {
    /**
     * Loads & saves the ingestion configuration for a repository.
     *
     * @param owner GitHub organization
     * @param repository GitHub repository name
     * @param branch Git branch
     * @param path Path to the ingestion repository
     */
    fun saveConfig(owner: String, repository: String, branch: String, path: String)

    /**
     * Removes the ingestion configuration for a repository.
     *
     * @param owner GitHub organization
     * @param repository GitHub repository name
     * @param branch Git branch
     */
    fun removeConfig(owner: String, repository: String, branch: String)
}
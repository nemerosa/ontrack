package net.nemerosa.ontrack.extension.av.config

/**
 * Configuration of the auto versioning on a branch.
 */
data class AutoVersioningConfig(
    val configurations: List<AutoVersioningSourceConfig>,
) {
    /**
     * Validates that the configuration is OK.
     *
     * @see AutoVersioningSourceConfig.validate
     */
    fun validate() {
        // Individual validation
        configurations.forEach { it.validate() }
        // Check that projects are uniques
        val projects = configurations.groupBy { it.sourceProject }
        val duplicates = projects.filter { (_, configs) ->
            configs.size > 1
        }.map { (project, _) ->
            project
        }
        if (duplicates.isNotEmpty()) {
            throw AutoVersioningConfigDuplicateProjectException(duplicates)
        }
    }

    fun postDeserialize() = AutoVersioningConfig(
        configurations = configurations.map { it.postDeserialize() }
    )
}

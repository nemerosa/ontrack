package net.nemerosa.ontrack.extension.av.property

/**
 * Configuration of the auto versioning on a branch.
 */
data class AutoVersioningProperty(
    val configurations: List<AutoVersioningConfig>,
) {
    /**
     * Validates that the configuration is OK.
     *
     * @see AutoVersioningConfig.validate
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
}

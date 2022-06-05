package net.nemerosa.ontrack.extension.av.config

import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.Project

/**
 * Service to configure branches for auto versioning.
 */
interface AutoVersioningConfigurationService {

    /**
     * Setups the auto versioning for a branch.
     *
     * @param branch Branch to configure
     * @param config Configuration to set (null to remove)
     */
    fun setupAutoVersioning(
        branch: Branch,
        config: AutoVersioningConfig?,
    )

    /**
     * Gets the auto versioning configuration for a branch
     *
     * @param branch Branch to get the configuration for
     * @return Auto versioning configuration or null if none is defined
     */
    fun getAutoVersioning(branch: Branch): AutoVersioningConfig?

    /**
     * Gets the list of branches which are configured for auto versioning for the given [project] and [promotion].
     */
    fun getBranchesConfiguredFor(project: String, promotion: String): List<Branch>

    /**
     * Gets the latest eligible branch in a source project, based on the criteria of an auto versioning configuration.
     */
    fun getLatestBranch(project: Project, config: AutoVersioningSourceConfig): Branch?

}
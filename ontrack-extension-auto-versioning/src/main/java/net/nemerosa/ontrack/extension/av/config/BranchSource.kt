package net.nemerosa.ontrack.extension.av.config

import net.nemerosa.ontrack.model.structure.Branch
import net.nemerosa.ontrack.model.structure.Project

/**
 * Mechanism to get the last source branch given a configuration and a target branch
 * for the auto versioning.
 */
interface BranchSource {

    /**
     * Gets the last source branch for the auto versioning.
     *
     * @param config Configuration for this branch source
     * @param project Source project
     * @param targetBranch Target branch for the auto versioning
     * @param promotion Promotion being done
     * @param includeDisabled True if the disabled branches must be included
     */
    fun getLatestBranch(
        config: String?,
        project: Project,
        targetBranch: Branch,
        promotion: String,
        includeDisabled: Boolean = false
    ): Branch?

    /**
     * Identifier for this branch source
     */
    val id: String

}
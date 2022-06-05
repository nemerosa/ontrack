package net.nemerosa.ontrack.extension.av.config

import net.nemerosa.ontrack.model.structure.Branch

/**
 * Association of a branch with its auto versioning configuration
 */
data class AutoVersioningConfiguredBranch(
    val branch: Branch,
    val configurations: List<AutoVersioningSourceConfig>,
)
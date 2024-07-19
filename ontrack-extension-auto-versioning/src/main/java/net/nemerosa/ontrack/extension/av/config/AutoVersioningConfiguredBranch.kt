package net.nemerosa.ontrack.extension.av.config

import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.structure.Branch

@APIDescription("Association of a branch with its auto versioning configuration")
data class AutoVersioningConfiguredBranch(
    @APIDescription("Target branch for the auto-versioning")
    val branch: Branch,
    @APIDescription("List of target auto-versioning configurations on the target branch")
    val configurations: List<AutoVersioningSourceConfig>,
)
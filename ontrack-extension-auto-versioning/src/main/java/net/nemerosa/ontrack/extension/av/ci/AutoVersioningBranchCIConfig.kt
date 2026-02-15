package net.nemerosa.ontrack.extension.av.ci

import net.nemerosa.ontrack.common.api.APIDescription
import net.nemerosa.ontrack.extension.av.config.AutoVersioningSourceConfig
import net.nemerosa.ontrack.graphql.support.ListRef

data class AutoVersioningBranchCIConfig(
    @APIDescription("Filter for the branches")
    val branchFilter: AutoVersioningBranchCIConfigBranchFilter = AutoVersioningBranchCIConfigBranchFilter(),
    @APIDescription("List of configurations")
    @ListRef
    val configurations: List<AutoVersioningSourceConfig>,
)

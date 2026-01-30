package net.nemerosa.ontrack.extension.av.graphql

import net.nemerosa.ontrack.common.api.APIDescription
import net.nemerosa.ontrack.extension.av.config.AutoVersioningSourceConfig
import net.nemerosa.ontrack.graphql.support.ListRef

@APIDescription("Auto versioning configuration")
data class SetAutoVersioningConfigInput(
    @APIDescription("ID of the branch to configure")
    val branchId: Int,
    @APIDescription("List of configurations")
    @ListRef(embedded = true, suffix = "Input")
    val configurations: List<AutoVersioningSourceConfig>,
)
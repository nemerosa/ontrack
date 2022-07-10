package net.nemerosa.ontrack.extension.av.graphql

import net.nemerosa.ontrack.extension.av.config.AutoVersioningSourceConfig
import net.nemerosa.ontrack.graphql.support.ListRef
import net.nemerosa.ontrack.model.annotations.APIDescription

@APIDescription("Auto versioning configuration identifying a branch by name")
data class SetAutoVersioningConfigByNameInput(
    @APIDescription("Name of the branch project to configure")
    val project: String,
    @APIDescription("Name of the branch to configure")
    val branch: String,
    /**
     * @see SetAutoVersioningConfigInput
     */
    @APIDescription("List of configurations")
    @ListRef(suffix = "Input")
    val configurations: List<AutoVersioningSourceConfig>,
)
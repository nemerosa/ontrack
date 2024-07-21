package net.nemerosa.ontrack.extension.av.graphql

import net.nemerosa.ontrack.model.annotations.APIDescription

@APIDescription("Deleting an auto-versioning configuration")
data class DeleteAutoVersioningConfigInput(
    @APIDescription("ID of the branch to clean")
    val branchId: Int,
)
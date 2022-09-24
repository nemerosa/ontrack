package net.nemerosa.ontrack.extension.av.graphql

import net.nemerosa.ontrack.model.annotations.APIDescription

@APIDescription("Description of a build")
data class CheckAutoVersioningInput(
    @APIDescription("Project name")
    val project: String,
    @APIDescription("Branch name")
    val branch: String,
    @APIDescription("Build name")
    val build: String,
)
package net.nemerosa.ontrack.extension.github.autoversioning

import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.annotations.APIName

@APIName("GitHubCheckAutoVersioningByBuildNameInput")
@APIDescription("Input for the auto versioning check for a build identified by its name")
data class CheckAutoVersioningByBuildNameInput(
    val owner: String,
    val repository: String,
    @APIDescription("Name of the build")
    val buildName: String,
)
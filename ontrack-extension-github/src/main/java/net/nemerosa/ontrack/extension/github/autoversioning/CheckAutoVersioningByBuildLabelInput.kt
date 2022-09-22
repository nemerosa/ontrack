package net.nemerosa.ontrack.extension.github.autoversioning

import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.annotations.APIName

@APIName("GitHubCheckAutoVersioningByBuildLabelInput")
@APIDescription("Input for the auto versioning check for a build identified by its label")
data class CheckAutoVersioningByBuildLabelInput(
    val owner: String,
    val repository: String,
    @APIDescription("Label of the build")
    val buildLabel: String,
)
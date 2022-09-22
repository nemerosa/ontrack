package net.nemerosa.ontrack.extension.github.autoversioning

import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.annotations.APIName

@APIName("GitHubCCheckAutoVersioningOutput")
data class CheckAutoVersioningOutput(
    @APIDescription("UUID of the payload being processed in the background")
    val uuid: String,
)
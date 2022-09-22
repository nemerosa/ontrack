package net.nemerosa.ontrack.extension.github.autoversioning

import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.annotations.APIName

@APIName("GitHubCheckAutoVersioningByRunIdInput")
@APIDescription("Input for the auto versioning check for a build identified by GHA workflow run ID")
data class CheckAutoVersioningByRunIdInput(
    val owner: String,
    val repository: String,
    @APIDescription("ID of the GHA workflow run")
    val runId: Long,
)
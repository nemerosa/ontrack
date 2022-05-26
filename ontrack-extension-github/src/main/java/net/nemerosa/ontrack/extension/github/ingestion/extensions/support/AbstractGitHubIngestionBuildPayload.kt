package net.nemerosa.ontrack.extension.github.ingestion.extensions.support

import com.fasterxml.jackson.annotation.JsonIgnore

abstract class AbstractGitHubIngestionBuildPayload(
    val owner: String,
    val repository: String,
    val runId: Long? = null,
    val buildName: String? = null,
    val buildLabel: String? = null,
) {
    @JsonIgnore
    open fun getSource() = if (runId != null) {
        "run=$runId"
    } else if (buildName != null) {
        "name=$buildName"
    } else if (buildLabel != null) {
        "label=$buildLabel"
    } else {
        error("No build identification")
    }
}
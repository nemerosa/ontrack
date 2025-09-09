package net.nemerosa.ontrack.extension.github.client

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class WorkflowRun(
    val id: Long,
    @JsonProperty("head_branch")
    val headBranch: String,
    val status: String,
)
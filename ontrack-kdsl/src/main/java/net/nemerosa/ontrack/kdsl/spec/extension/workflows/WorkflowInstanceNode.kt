package net.nemerosa.ontrack.kdsl.spec.extension.workflows

import com.fasterxml.jackson.databind.JsonNode

data class WorkflowInstanceNode(
    val id: String,
    val status: WorkflowInstanceNodeStatus,
    val output: JsonNode?,
    val error: String?,
)

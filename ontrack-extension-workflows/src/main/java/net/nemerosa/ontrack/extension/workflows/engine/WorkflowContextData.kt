package net.nemerosa.ontrack.extension.workflows.engine

import com.fasterxml.jackson.databind.JsonNode

data class WorkflowContextData(
    val key: String,
    val value: JsonNode,
)

package net.nemerosa.ontrack.extension.workflows.engine

import com.fasterxml.jackson.databind.JsonNode

/**
 * Execution information about a node
 *
 * @property id ID of the node
 */
data class WorkflowInstanceNode(
    val id: String,
    val status: WorkflowInstanceNodeStatus,
    val output: JsonNode?,
) {
    fun success(output: JsonNode) = WorkflowInstanceNode(
        id = id,
        status = WorkflowInstanceNodeStatus.SUCCESS,
        output = output,
    )
}

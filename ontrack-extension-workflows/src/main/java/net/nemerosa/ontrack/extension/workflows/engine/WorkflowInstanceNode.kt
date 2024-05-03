package net.nemerosa.ontrack.extension.workflows.engine

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.common.reducedStackTrace

/**
 * Execution information about a node
 *
 * @property id ID of the node
 */
data class WorkflowInstanceNode(
    val id: String,
    val status: WorkflowInstanceNodeStatus,
    val output: JsonNode?,
    val error: String?,
) {
    fun success(output: JsonNode) = WorkflowInstanceNode(
        id = id,
        status = WorkflowInstanceNodeStatus.SUCCESS,
        output = output,
        error = null,
    )

    fun error(throwable: Throwable) = WorkflowInstanceNode(
        id = id,
        status = WorkflowInstanceNodeStatus.ERROR,
        output = null,
        error = reducedStackTrace(throwable),
    )
}

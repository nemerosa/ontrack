package net.nemerosa.ontrack.kdsl.spec.extension.workflows

import com.fasterxml.jackson.databind.JsonNode

data class WorkflowInstance(
    val status: WorkflowInstanceStatus,
    val finished: Boolean,
    val nodesExecutions: List<WorkflowInstanceNode>,
) {
    fun getExecutionOutput(nodeId: String): JsonNode? {
        return nodesExecutions.firstOrNull { it.id == nodeId }?.output
    }
}

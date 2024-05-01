package net.nemerosa.ontrack.extension.workflows.engine

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.workflows.definition.Workflow

/**
 * Information about the execution of a workflow.
 *
 * @property id Unique ID for this workflow instance.
 * @property workflow Associated workflow
 * @property executorId ID of the workflow node executor
 * @property nodesExecutions Information about the node executions
 * @property status Status of the execution of this workflow
 */
data class WorkflowInstance(
    val id: String,
    val workflow: Workflow,
    val executorId: String,
    val nodesExecutions: List<WorkflowInstanceNode>,
) {

    val status: WorkflowInstanceStatus
        get() {
            val nodes = nodesExecutions.map { it.status }
            if (nodes.all { it == WorkflowInstanceNodeStatus.IDLE }) {
                return WorkflowInstanceStatus.STARTED
            } else if (nodes.any { it == WorkflowInstanceNodeStatus.ERROR }) {
                return WorkflowInstanceStatus.ERROR
            } else if (nodes.any { it == WorkflowInstanceNodeStatus.STARTED }) {
                return WorkflowInstanceStatus.RUNNING
            } else if (nodes.all { it == WorkflowInstanceNodeStatus.SUCCESS }) {
                return WorkflowInstanceStatus.SUCCESS
            } else {
                error("Inconsistent state")
            }
        }

    fun successNode(id: String, output: JsonNode) = WorkflowInstance(
        id = id,
        workflow = workflow,
        executorId = executorId,
        nodesExecutions = nodesExecutions.map { node ->
            if (node.id == id) {
                node.success(output)
            } else {
                node
            }
        },
    )
}


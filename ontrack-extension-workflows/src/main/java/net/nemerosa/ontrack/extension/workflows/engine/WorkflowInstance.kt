package net.nemerosa.ontrack.extension.workflows.engine

import com.fasterxml.jackson.annotation.JsonIgnore
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

    @get:JsonIgnore
    val status: WorkflowInstanceStatus
        get() {
            val nodes = nodesExecutions.map { it.status }
            return if (nodes.any { it == WorkflowInstanceNodeStatus.ERROR }) {
                WorkflowInstanceStatus.ERROR
            } else if (nodes.all { it == WorkflowInstanceNodeStatus.SUCCESS }) {
                WorkflowInstanceStatus.SUCCESS
            } else if (nodes.any { it == WorkflowInstanceNodeStatus.STARTED }) {
                WorkflowInstanceStatus.RUNNING
            } else {
                WorkflowInstanceStatus.STARTED
            }
        }

    fun successNode(nodeId: String, output: JsonNode) = WorkflowInstance(
        id = id,
        workflow = workflow,
        executorId = executorId,
        nodesExecutions = nodesExecutions.map { node ->
            if (node.id == nodeId) {
                node.success(output)
            } else {
                node
            }
        },
    )

    fun getNode(nodeId: String) = nodesExecutions.firstOrNull { it.id == nodeId }
        ?: throw WorkflowNodeNotFoundException(nodeId)

}


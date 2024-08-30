package net.nemerosa.ontrack.extension.workflows.execution

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.workflows.engine.WorkflowInstance
import net.nemerosa.ontrack.model.extension.Extension

/**
 * Registerable workflow callback for the execution of the nodes.
 */
interface WorkflowNodeExecutor : Extension {

    /**
     * ID of the executor
     */
    val id: String

    /**
     * Display name for the executor
     */
    val displayName: String

    /**
     * Runs some action for a given workflow node.
     *
     * @param workflowInstance Workflow to run
     * @param workflowNodeId Workflow node
     * @return Result for the node execution
     */
    suspend fun execute(
        workflowInstance: WorkflowInstance,
        workflowNodeId: String,
        workflowNodeExecutorResultFeedback: (output: JsonNode?) -> Unit,
    ): WorkflowNodeExecutorResult

}
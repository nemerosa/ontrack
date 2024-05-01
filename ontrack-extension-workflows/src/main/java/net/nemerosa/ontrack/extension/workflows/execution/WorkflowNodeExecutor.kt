package net.nemerosa.ontrack.extension.workflows.execution

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.workflows.engine.WorkflowInstance
import net.nemerosa.ontrack.extension.workflows.engine.WorkflowInstanceNode
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
     * Runs some action for a given workflow node.
     *
     * @param workflowInstance Workflow to run
     * @param workflowInstanceNode Workflow node to run
     * @return Outcome for the node execution
     */
    fun execute(
        workflowInstance: WorkflowInstance,
        workflowInstanceNode: WorkflowInstanceNode,
    ): JsonNode

}
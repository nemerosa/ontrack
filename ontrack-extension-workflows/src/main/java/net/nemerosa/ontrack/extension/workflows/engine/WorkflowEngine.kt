package net.nemerosa.ontrack.extension.workflows.engine

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.workflows.definition.Workflow
import net.nemerosa.ontrack.extension.workflows.execution.WorkflowNodeExecutor

/**
 * Engine used to orchestrate the execution of workflows.
 */
interface WorkflowEngine {

    /**
     * Starts the execution of a workflow.
     *
     * @param workflow Workflow to run
     * @param context Execution context
     * @return Initial state of the workflow instance
     */
    fun startWorkflow(workflow: Workflow, context: JsonNode): WorkflowInstance

    /**
     * Given the ID of a [WorkflowInstance], returns this instance or null if not found.
     */
    fun findWorkflowInstance(id: String): WorkflowInstance?

    /**
     * Processing one node in a workflow
     */
    fun processNode(workflowInstanceId: String, workflowNodeId: String)

}
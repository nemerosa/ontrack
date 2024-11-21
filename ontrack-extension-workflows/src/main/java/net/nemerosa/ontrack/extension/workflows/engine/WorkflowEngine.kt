package net.nemerosa.ontrack.extension.workflows.engine

import net.nemerosa.ontrack.extension.workflows.definition.Workflow
import net.nemerosa.ontrack.model.events.SerializableEvent

/**
 * Engine used to orchestrate the execution of workflows.
 */
interface WorkflowEngine {

    /**
     * Starts the execution of a workflow.
     *
     * @param workflow Workflow to run
     * @param event Execution context
     * @param callback Function to call when the workflow is finished (successful or in error)
     * @return Initial state of the workflow instance
     */
    fun startWorkflow(
        workflow: Workflow,
        event: SerializableEvent,
        callback: (instance: WorkflowInstance) -> Unit,
    ): WorkflowInstance

    /**
     * Given the ID of a [WorkflowInstance], returns this instance or null if not found.
     */
    fun findWorkflowInstance(id: String): WorkflowInstance?

    /**
     * Stops a workflow and all its nodes
     */
    fun stopWorkflow(workflowInstanceId: String)

}
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
     * @param pauseMs Pause before launching the workflow (used for tests)
     * @return Initial state of the workflow instance
     */
    fun startWorkflow(
        workflow: Workflow,
        event: SerializableEvent,
        pauseMs: Long = 0,
    ): WorkflowInstance

    /**
     * Given the ID of a [WorkflowInstance], returns this instance or null if not found.
     */
    fun findWorkflowInstance(id: String): WorkflowInstance?

    /**
     * Processing one node in a workflow
     */
    fun processNode(workflowInstanceId: String, workflowNodeId: String)

    /**
     * Stops a workflow and all its nodes
     */
    fun stopWorkflow(workflowInstanceId: String)

}
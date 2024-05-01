package net.nemerosa.ontrack.extension.workflows.engine

import net.nemerosa.ontrack.extension.workflows.definition.Workflow

/**
 * Information about the execution of a workflow.
 *
 * @property id Unique ID for this workflow instance.
 * @property workflow Associated workflow
 * @property nodesExecutions Informations about the node executions
 */
data class WorkflowInstance(
    val id: String,
    val workflow: Workflow,
    val nodesExecutions: List<WorkflowInstanceNode>,
)

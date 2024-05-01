package net.nemerosa.ontrack.extension.workflows.engine

import net.nemerosa.ontrack.extension.workflows.definition.Workflow
import net.nemerosa.ontrack.extension.workflows.definition.WorkflowNode
import net.nemerosa.ontrack.extension.workflows.execution.WorkflowNodeExecutor
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
@Transactional
class WorkflowEngineImpl(
    private val workflowInstanceStore: WorkflowInstanceStore,
) : WorkflowEngine {

    override fun startWorkflow(workflow: Workflow, workflowNodeExecutor: WorkflowNodeExecutor): WorkflowInstance {
        // TODO Checks the workflow consistency (cycles, etc.) - use a public method, usable by extensions
        // Creating the instance
        val instance = WorkflowInstance(
            id = UUID.randomUUID().toString(),
            workflow = workflow,
            nodesExecutions = workflow.nodes.map { it.toStartExecution() },
            status = WorkflowInstanceStatus.STARTED,
        )
        // Storing the instance
        workflowInstanceStore.store(instance)
        // TODO Getting the starting nodes
        // Returning the instance
        return instance
    }

    override fun findWorkflowInstance(id: String): WorkflowInstance? =
        workflowInstanceStore.findById(id)

    private fun WorkflowNode.toStartExecution() = WorkflowInstanceNode(
        id = id,
    )

}
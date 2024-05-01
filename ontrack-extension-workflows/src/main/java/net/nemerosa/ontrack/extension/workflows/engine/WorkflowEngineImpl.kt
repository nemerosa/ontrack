package net.nemerosa.ontrack.extension.workflows.engine

import net.nemerosa.ontrack.extension.api.ExtensionManager
import net.nemerosa.ontrack.extension.queue.dispatching.QueueDispatcher
import net.nemerosa.ontrack.extension.workflows.definition.Workflow
import net.nemerosa.ontrack.extension.workflows.execution.WorkflowNodeExecutor
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class WorkflowEngineImpl(
    private val workflowInstanceStore: WorkflowInstanceStore,
    private val queueDispatcher: QueueDispatcher,
    private val workflowQueueProcessor: WorkflowQueueProcessor,
    private val extensionManager: ExtensionManager,
) : WorkflowEngine {

    override fun startWorkflow(workflow: Workflow, workflowNodeExecutor: WorkflowNodeExecutor): WorkflowInstance {
        // TODO Checks the workflow consistency (cycles, etc.) - use a public method, usable by extensions
        // Creating the instance
        val instance = createInstance(workflow, workflowNodeExecutor)
        // Storing the instance
        workflowInstanceStore.store(instance)
        // Getting the starting nodes
        val nodes = instance.workflow.getNextNodes(null)
        // TODO Special case: no starting node
        // Scheduling the nodes
        for (nodeId in nodes) {
            queueNode(workflowNodeExecutor, instance, nodeId)
        }
        // Returning the instance
        return instance
    }

    private fun queueNode(
        workflowNodeExecutor: WorkflowNodeExecutor,
        instance: WorkflowInstance,
        nodeId: String
    ) {
        queueDispatcher.dispatch(
            queueProcessor = workflowQueueProcessor,
            payload = WorkflowQueuePayload(
                workflowNodeExecutorId = workflowNodeExecutor.id,
                workflowInstanceId = instance.id,
                workflowNodeId = nodeId,
            ),
            source = null, // TODO Setting a custom source
        )
    }

    override fun processNode(workflowInstanceId: String, workflowNodeId: String) {
        // Getting the instance & the node
        var instance = getWorkflowInstance(workflowInstanceId)
        val node = instance.workflow.getNode(workflowNodeId)
        // TODO Running the node in the instance
        // Getting the node executor
        val executor = extensionManager.getExtensions(WorkflowNodeExecutor::class.java)
            .find { it.id == instance.executorId }
            ?: throw WorkflowNodeExecutorNotFoundException(instance.executorId)
        // Running the executor
        try {
            val output = executor.execute(instance, node.id)
            // Stores the output back into the instance and progresses the node's status
            instance = workflowInstanceStore.store(instance.successNode(node.id, output))
            // Getting the next nodes
            val nextNodes = instance.workflow.getNextNodes(node.id)
            for (nextNode in nextNodes) {
                // TODO For each next node, checks if it can be scheduled or not
                // Schedule the node
                queueNode(executor, instance, nextNode)
            }
        } catch (any: Exception) {
            // TODO Stores the node error status
        }
    }

    override fun findWorkflowInstance(id: String): WorkflowInstance? =
        workflowInstanceStore.findById(id)

}
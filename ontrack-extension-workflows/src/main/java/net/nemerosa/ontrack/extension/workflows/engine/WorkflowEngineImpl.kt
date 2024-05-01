package net.nemerosa.ontrack.extension.workflows.engine

import net.nemerosa.ontrack.extension.queue.dispatching.QueueDispatcher
import net.nemerosa.ontrack.extension.workflows.definition.Workflow
import net.nemerosa.ontrack.extension.workflows.execution.WorkflowNodeExecutor
import net.nemerosa.ontrack.extension.workflows.execution.WorkflowNodeExecutorService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class WorkflowEngineImpl(
    private val workflowInstanceStore: WorkflowInstanceStore,
    private val queueDispatcher: QueueDispatcher,
    private val workflowQueueProcessor: WorkflowQueueProcessor,
    private val workflowNodeExecutorService: WorkflowNodeExecutorService,
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
        // Getting the node executor
        val executor = workflowNodeExecutorService.getExecutor(instance.executorId)
        // Running the executor
        try {
            val output = executor.execute(instance, node.id)
            // Stores the output back into the instance and progresses the node's status
            instance = workflowInstanceStore.store(instance.successNode(node.id, output))
            // Getting the next nodes
            val nextNodes = instance.workflow.getNextNodes(node.id)
            for (nextNode in nextNodes) {
                // For each next node, checks if it can be scheduled or not
                if (canRunNode(instance, nextNode)) {
                    // Schedule the node
                    queueNode(executor, instance, nextNode)
                }
            }
        } catch (any: Exception) {
            // TODO Stores the node error status
        }
    }

    private fun canRunNode(instance: WorkflowInstance, nodeId: String): Boolean {
        // Getting the instance node state
        val instanceNode = instance.getNode(nodeId)
        // Getting the workflow node
        val workflowNode = instance.workflow.getNode(nodeId)
        // Running all the checks
        // 1. node must be idle
        return instanceNode.status == WorkflowInstanceNodeStatus.IDLE &&
                // 2. all its parents must be in SUCCESS state
                workflowNode.parents.all { parent ->
                    val parentNode = instance.getNode(parent.id)
                    parentNode.status == WorkflowInstanceNodeStatus.SUCCESS
                }
    }

    override fun findWorkflowInstance(id: String): WorkflowInstance? =
        workflowInstanceStore.findById(id)

}
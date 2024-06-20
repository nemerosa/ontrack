package net.nemerosa.ontrack.extension.workflows.engine

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import net.nemerosa.ontrack.extension.queue.dispatching.QueueDispatcher
import net.nemerosa.ontrack.extension.queue.source.createQueueSource
import net.nemerosa.ontrack.extension.workflows.definition.Workflow
import net.nemerosa.ontrack.extension.workflows.definition.WorkflowValidation
import net.nemerosa.ontrack.extension.workflows.execution.WorkflowNodeExecutorService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Duration

@Service
@Transactional
class WorkflowEngineImpl(
    private val workflowInstanceStore: WorkflowInstanceStore,
    private val queueDispatcher: QueueDispatcher,
    private val workflowQueueProcessor: WorkflowQueueProcessor,
    private val workflowNodeExecutorService: WorkflowNodeExecutorService,
    private val workflowQueueSourceExtension: WorkflowQueueSourceExtension,
) : WorkflowEngine {

    private val logger: Logger = LoggerFactory.getLogger(WorkflowEngine::class.java)

    override fun startWorkflow(
        workflow: Workflow,
        context: WorkflowContext,
    ): WorkflowInstance {
        // Checks the workflow consistency (cycles, etc.) - use a public method, usable by extensions
        WorkflowValidation.validateWorkflow(workflow).throwErrorIfAny()
        // Creating the instance
        val instance = createInstance(workflow, context)
        // Storing the instance
        workflowInstanceStore.store(instance)
        // Getting the starting nodes
        val nodes = instance.workflow.getNextNodes(null)
        // Scheduling the nodes
        for (nodeId in nodes) {
            queueNode(instance, nodeId)
        }
        // Returning the instance
        return instance
    }

    override fun stopWorkflow(workflowInstanceId: String) {
        // Getting the instance
        val instance = getWorkflowInstance(workflowInstanceId)
        // Stopping all unfinished nodes
        instance.nodesExecutions.forEach { nodeExecution ->
            val nodeId = nodeExecution.id
            if (!nodeExecution.status.finished) {
                workflowInstanceStore.store(instance.stopNode(nodeId))
            }
        }
    }

    private fun queueNode(
        instance: WorkflowInstance,
        nodeId: String
    ) {
        val node = instance.workflow.getNode(nodeId)
        queueDispatcher.dispatch(
            queueProcessor = workflowQueueProcessor,
            payload = WorkflowQueuePayload(
                workflowInstanceId = instance.id,
                workflowNodeId = node.id,
                workflowNodeExecutorId = node.executorId,
            ),
            source = workflowQueueSourceExtension.createQueueSource(
                WorkflowQueueSourceData(
                    workflowInstanceId = instance.id,
                    workflowNodeId = node.id,
                    workflowNodeExecutorId = node.executorId,
                )
            )
        )
    }

    override fun processNode(workflowInstanceId: String, workflowNodeId: String) {
        // Getting the instance & the node
        var instance = getWorkflowInstance(workflowInstanceId)
        val node = instance.workflow.getNode(workflowNodeId)
        val instanceNode = instance.getNode(workflowNodeId)
        // Checking the node status
        val nodeStatus = instanceNode.status
        if (nodeStatus == WorkflowInstanceNodeStatus.IDLE) {
            try {
                // Starting the node
                instance = workflowInstanceStore.store(instance.startNode(node.id))
                // Getting the node executor
                val executor = workflowNodeExecutorService.getExecutor(node.executorId)
                // Timeout
                val timeout = Duration.ofSeconds(node.timeout)
                // Running the executor
                val output = runBlocking {
                    withTimeoutOrNull(timeout.toMillis()) {
                        executor.execute(instance, node.id)
                    }
                }
                // Timeout?
                if (output == null) {
                    throw WorkflowExecutionTimeoutException(timeout)
                }
                // Stores the output back into the instance and progresses the node's status
                instance = workflowInstanceStore.store(instance.successNode(node.id, output))
                // Getting the next nodes
                val nextNodes = instance.workflow.getNextNodes(node.id)
                for (nextNode in nextNodes) {
                    // For each next node, checks if it can be scheduled or not
                    if (canRunNode(instance, nextNode)) {
                        // Schedule the node
                        queueNode(instance, nextNode)
                    }
                }
            } catch (any: Throwable) {
                // Stores the node error status
                workflowInstanceStore.store(instance.errorNode(node.id, any))
                // Stopping the workflow
                stopWorkflow(workflowInstanceId)
            }
        } else {
            logger.warn("Node already started, should not be processed. workflowInstanceId=$workflowInstanceId,workflowNodeId=$workflowNodeId")
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
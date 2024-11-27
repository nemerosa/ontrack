package net.nemerosa.ontrack.extension.workflows.engine

import com.fasterxml.jackson.databind.JsonNode
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import net.nemerosa.ontrack.extension.queue.dispatching.QueueDispatcher
import net.nemerosa.ontrack.extension.queue.source.createQueueSource
import net.nemerosa.ontrack.extension.workflows.definition.Workflow
import net.nemerosa.ontrack.extension.workflows.definition.WorkflowValidation
import net.nemerosa.ontrack.extension.workflows.execution.WorkflowNodeExecutorResultType
import net.nemerosa.ontrack.extension.workflows.execution.WorkflowNodeExecutorService
import net.nemerosa.ontrack.model.events.SerializableEvent
import net.nemerosa.ontrack.model.tx.TransactionHelper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Duration

@Service
@ConditionalOnProperty(
    prefix = "ontrack.config.extension.workflows",
    name = ["mode"],
    havingValue = "LEGACY",
    matchIfMissing = false,
)
@Transactional
class WorkflowEngineImpl(
    private val workflowInstanceStore: WorkflowInstanceStore,
    private val queueDispatcher: QueueDispatcher,
    private val workflowQueueProcessor: WorkflowQueueProcessor,
    private val workflowNodeExecutorService: WorkflowNodeExecutorService,
    private val workflowQueueSourceExtension: WorkflowQueueSourceExtension,
    private val transactionHelper: TransactionHelper,
) : WorkflowEngine {

    private val logger: Logger = LoggerFactory.getLogger(WorkflowEngine::class.java)

    override fun startWorkflow(
        workflow: Workflow,
        event: SerializableEvent,
        pauseMs: Long,
    ): WorkflowInstance {
        // Checks the workflow consistency (cycles, etc.) - use a public method, usable by extensions
        WorkflowValidation.validateWorkflow(workflow).throwErrorIfAny()
        // Creating the instance
        val instance = createInstance(workflow = workflow, event = event)
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
        workflowInstanceStore.store(instance.stopNodes())
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
        if (nodeStatus == WorkflowInstanceNodeStatus.CREATED) {
            try {
                // Starting the node
                instance = workflowInstanceStore.store(instance.startNode(node.id))
                // Getting the node executor
                val executor = workflowNodeExecutorService.getExecutor(node.executorId)
                // Timeout
                val timeout = Duration.ofSeconds(node.timeout)

                // Continuous feedback for the node
                val nodeFeedback: (output: JsonNode?) -> Unit = { output: JsonNode? ->
                    if (output != null) {
                        transactionHelper.inNewTransaction {
                            workflowInstanceStore.store(instance.progressNode(node.id, output))
                        }
                    }
                }

                // Running the executor
                val result = runBlocking {
                    withTimeoutOrNull(timeout.toMillis()) {
                        val deferred = async {
                            executor.execute(instance, node.id, nodeFeedback)
                        }
                        deferred.await()
                    }
                }
                // Timeout?
                if (result == null) {
                    throw WorkflowExecutionTimeoutException(timeout)
                }
                // Progressing the instance or stopping it in case of error
                when (result.type) {
                    WorkflowNodeExecutorResultType.ERROR -> {
                        workflowInstanceStore.store(
                            instance.errorNode(
                                node.id,
                                throwable = null,
                                message = result.message,
                                output = result.output,
                            )
                        )
                        stopWorkflow(workflowInstanceId)
                    }

                    WorkflowNodeExecutorResultType.SUCCESS -> {
                        // Stores the output back into the instance and progresses the node's status
                        workflowInstanceStore.store(
                            instance.successNode(
                                nodeId = node.id,
                                output = result.output ?: error("Missing notification output"),
                                eventToMerge = result.event,
                            )
                        )
                        // Loads the current state of the instance
                        instance = getWorkflowInstance(workflowInstanceId)
                        // Getting the next nodes
                        val nextNodes = instance.workflow.getNextNodes(node.id)
                        for (nextNode in nextNodes) {
                            // For each next node, checks if it can be scheduled or not
                            if (canRunNode(instance, nextNode)) {
                                // Schedule the node
                                queueNode(instance, nextNode)
                            }
                        }
                    }
                }
            } catch (any: Throwable) {
                // Stores the node error status
                workflowInstanceStore.store(instance.errorNode(node.id, throwable = any, message = null, output = null))
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
        return instanceNode.status == WorkflowInstanceNodeStatus.CREATED &&
                // 2. all its parents must be in SUCCESS state
                workflowNode.parents.all { parent ->
                    val parentNode = instance.getNode(parent.id)
                    parentNode.status == WorkflowInstanceNodeStatus.SUCCESS
                }
    }

    override fun findWorkflowInstance(id: String): WorkflowInstance? =
        workflowInstanceStore.findById(id)

}
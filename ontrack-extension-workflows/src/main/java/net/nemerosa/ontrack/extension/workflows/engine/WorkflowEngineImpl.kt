package net.nemerosa.ontrack.extension.workflows.engine

import com.fasterxml.jackson.databind.JsonNode
import kotlinx.coroutines.*
import net.nemerosa.ontrack.extension.queue.dispatching.QueueDispatcher
import net.nemerosa.ontrack.extension.queue.source.createQueueSource
import net.nemerosa.ontrack.extension.workflows.WorkflowConfigurationProperties
import net.nemerosa.ontrack.extension.workflows.definition.Workflow
import net.nemerosa.ontrack.extension.workflows.definition.WorkflowParentNode
import net.nemerosa.ontrack.extension.workflows.definition.WorkflowValidation
import net.nemerosa.ontrack.extension.workflows.execution.WorkflowNodeExecutorResultType
import net.nemerosa.ontrack.extension.workflows.execution.WorkflowNodeExecutorService
import net.nemerosa.ontrack.extension.workflows.repository.WorkflowInstanceRepository
import net.nemerosa.ontrack.model.events.SerializableEvent
import net.nemerosa.ontrack.model.tx.DefaultTransactionHelper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.PlatformTransactionManager
import java.time.Duration

@Component
class WorkflowEngineImpl(
    private val workflowInstanceRepository: WorkflowInstanceRepository,
    private val queueDispatcher: QueueDispatcher,
    private val workflowQueueProcessor: WorkflowQueueProcessor,
    private val workflowQueueSourceExtension: WorkflowQueueSourceExtension,
    private val workflowNodeExecutorService: WorkflowNodeExecutorService,
    private val workflowConfigurationProperties: WorkflowConfigurationProperties,
    platformTransactionManager: PlatformTransactionManager,
) : WorkflowEngine {

    private val logger: Logger = LoggerFactory.getLogger(WorkflowEngineImpl::class.java)

    private val transactionHelper = DefaultTransactionHelper(platformTransactionManager)

    override fun startWorkflow(
        workflow: Workflow,
        event: SerializableEvent,
        pauseMs: Long,
    ): WorkflowInstance {
        // Checks the workflow consistency (cycles, etc.) - use a public method, usable by extensions
        WorkflowValidation.validateWorkflow(workflow).throwErrorIfAny()

        // Adapting the workflow with additional nodes
        var actualWorkflow = workflow
        // Pause node
        if (pauseMs > 0) {
            actualWorkflow = actualWorkflow.addNodeBeforeEach(pauseNode(pauseMs))
        }
        // TODO Termination node

        // Creating the instance
        val instance = createInstance(workflow = actualWorkflow, event = event)

        // Storing the instance
        transactionHelper.inNewTransaction {
            workflowInstanceRepository.createInstance(instance)
        }

        for (node in instance.workflow.nodes) {
            queueDispatcher.dispatch(
                queueProcessor = workflowQueueProcessor,
                payload = WorkflowQueuePayload(
                    workflowInstanceId = instance.id,
                    workflowNodeId = node.id,
                ),
                source = workflowQueueSourceExtension.createQueueSource(
                    WorkflowQueueSourceData(
                        workflowInstanceId = instance.id,
                        workflowNodeId = node.id,
                    )
                )
            )
        }

        // OK
        return instance
    }

    override fun processNode(workflowInstanceId: String, workflowNodeId: String) {
        // Getting the instance
        val instance = getWorkflowInstanceTx(workflowInstanceId)
        val node = instance.workflow.getNode(workflowNodeId)
        val nodeExecution = instance.getNode(workflowNodeId)
        logger.debug("{} INSTANCE {} NODE RECEIVED", workflowInstanceId, workflowNodeId)
        // Checks of the instance has been interrupted or not
        if (instance.status.finished) {
            logger.debug("{} INSTANCE {} NODE INSTANCE FINISHED", workflowInstanceId, workflowNodeId)
            return
        }
        // Checks the instance node status
        if (nodeExecution.status.finished) {
            logger.debug("{} INSTANCE {} NODE FINISHED", workflowInstanceId, workflowNodeId)
            return
        } else if (nodeExecution.status != WorkflowInstanceNodeStatus.CREATED) {
            logger.error("{} INSTANCE {} NODE NOT IN CREATED STATE", workflowInstanceId, workflowNodeId)
            return
        }
        // Starting the job for the node
        CoroutineScope(Dispatchers.Default).launch {
            logger.debug("{} INSTANCE {} NODE PROCESSING", workflowInstanceId, workflowNodeId)
            // Changes the node's status to WAITING
            logger.debug("{} INSTANCE {} NODE WAITING", workflowInstanceId, workflowNodeId)
            nodeWaiting(workflowInstanceId, workflowNodeId)
            // Waiting for the parent nodes to be OK
            val okToStart = if (node.parents.isNotEmpty()) {
                val parentJobs = node.parents.map {
                    createParentJob(this, instance, it)
                }
                logger.debug("{} INSTANCE {} NODE WAITING FOR PARENTS", workflowInstanceId, workflowNodeId)
                val parentStatuses = parentJobs.awaitAll()
                logger.debug("{} INSTANCE {} NODE WAITED FOR PARENTS", workflowInstanceId, workflowNodeId)
                // TODO OK to start depending on the conditions
                val parentsOK = parentStatuses.all { it.status == WorkflowInstanceNodeStatus.SUCCESS }
                // Checking the instance state again
                if (parentsOK) {
                    !getWorkflowInstanceTx(workflowInstanceId).status.finished
                } else {
                    false
                }
            } else {
                // No parent
                true
            }
            // Starting the node execution
            if (okToStart) {
                logger.debug("{} INSTANCE {} NODE STARTED", workflowInstanceId, workflowNodeId)
                nodeStarted(workflowInstanceId, workflowNodeId)
                // Loading a fresh instance before starting
                val freshInstance = getWorkflowInstanceTx(workflowInstanceId)
                // Starts the node execution
                nodeExecution(freshInstance, workflowNodeId)
            } else {
                logger.debug("{} INSTANCE {} NODE PARENT NOT OK", workflowInstanceId, workflowNodeId)
                nodeCancelled(workflowInstanceId, workflowNodeId, "Parents conditions were not met.")
            }
        }
        logger.debug("{} INSTANCE {} NODE LAUNCHED", workflowInstanceId, workflowNodeId)
    }

    private fun getWorkflowInstanceTx(workflowInstanceId: String) = transactionHelper.inNewTransaction {
        workflowInstanceRepository.findWorkflowInstance(workflowInstanceId)
            ?: error("Could not find the workflow instance: $workflowInstanceId")
    }

    private fun createParentJob(
        coroutineScope: CoroutineScope,
        instance: WorkflowInstance,
        parentDef: WorkflowParentNode
    ): Deferred<WorkflowParentStatus> {
        val parentNode = instance.workflow.getNode(parentDef.id)
        val parentTimeoutMs = parentNode.timeout * 1_000L
        return coroutineScope.async {
            val status = withTimeoutOrNull(parentTimeoutMs) {
                var parentFinished = false
                var parentStatus: WorkflowInstanceNodeStatus? = null
                while (!parentFinished) {
                    parentStatus = getNodeStatus(instance.id, parentDef.id)
                        ?: error("Could not find status for parent node: ${instance.id} / ${parentDef.id}")
                    if (parentStatus.finished) {
                        parentFinished = true
                    } else {
                        delay(workflowConfigurationProperties.parentWaitingInterval.toMillis())
                    }
                }
                parentStatus
            }
            WorkflowParentStatus(parentDef, status)
        }
    }

    private suspend fun nodeExecution(instance: WorkflowInstance, nodeId: String) {
        // Getting the node
        val node = instance.workflow.getNode(nodeId)
        // Getting the node executor
        val executor = workflowNodeExecutorService.getExecutor(node.executorId)
        // Timeout
        val timeout = Duration.ofSeconds(node.timeout)

        // Continuous feedback for the node
        val nodeFeedback: (output: JsonNode?) -> Unit = { output: JsonNode? ->
            if (output != null) {
                nodeProgress(instance.id, nodeId, output)
            }
        }

        try {

            // Running the executor
            val result = withTimeoutOrNull(timeout.toMillis()) {
                val deferred = async {
                    logger.debug("{} INSTANCE {} NODE EXECUTING", instance.id, nodeId)
                    executor.execute(instance, node.id, nodeFeedback)
                }
                val outcome = deferred.await()
                logger.debug("{} INSTANCE {} NODE EXECUTED (type = {})", instance.id, nodeId, outcome.type)
                outcome
            }

            // Timeout?
            if (result == null) {
                logger.debug("{} INSTANCE {} NODE TIMEOUT", instance.id, nodeId)
                nodeError(instance.id, nodeId, "Timeout", null)
            }
            // Progressing the instance or stopping it in case of error
            else {
                when (result.type) {
                    WorkflowNodeExecutorResultType.ERROR -> {
                        logger.debug("{} INSTANCE {} NODE ERROR", instance.id, nodeId)
                        nodeError(instance.id, nodeId, result.message, result.output)
                    }

                    WorkflowNodeExecutorResultType.SUCCESS -> {
                        logger.debug("{} INSTANCE {} NODE SUCCESS", instance.id, nodeId)
                        // Stores the output back into the instance and progresses the node's status
                        nodeSuccess(instance.id, nodeId, result.output, result.event)
                    }
                }
            }
        } catch (any: Throwable) {
            // Stores the node error status
            logger.error("${instance.id} INSTANCE $nodeId NODE UNCAUGHT ERROR", any)
            nodeError(instance.id, nodeId, any.message, null)
        }
    }

    private fun getNodeStatus(instanceId: String, nodeId: String): WorkflowInstanceNodeStatus? =
        transactionHelper.inNewTransactionNullable {
            workflowInstanceRepository.getNodeStatus(instanceId, nodeId)
        }

    private fun nodeWaiting(workflowInstanceId: String, workflowNodeId: String) {
        transactionHelper.inNewTransaction {
            workflowInstanceRepository.nodeWaiting(workflowInstanceId, workflowNodeId)
        }
    }

    private fun nodeStarted(workflowInstanceId: String, workflowNodeId: String) {
        transactionHelper.inNewTransaction {
            workflowInstanceRepository.nodeStarted(workflowInstanceId, workflowNodeId)
        }
    }

    private fun nodeCancelled(workflowInstanceId: String, workflowNodeId: String, message: String) {
        transactionHelper.inNewTransaction {
            workflowInstanceRepository.nodeCancelled(workflowInstanceId, workflowNodeId, message)
        }
    }

    private fun nodeProgress(workflowInstanceId: String, workflowNodeId: String, output: JsonNode) {
        transactionHelper.inNewTransaction {
            workflowInstanceRepository.nodeProgress(workflowInstanceId, workflowNodeId, output)
        }
    }

    private fun nodeSuccess(
        workflowInstanceId: String,
        workflowNodeId: String,
        output: JsonNode?,
        event: SerializableEvent?
    ) {
        transactionHelper.inNewTransaction {
            workflowInstanceRepository.nodeSuccess(workflowInstanceId, workflowNodeId, output, event)
        }
    }

    private fun nodeError(instanceId: String, nodeId: String, message: String?, output: JsonNode?) {
        transactionHelper.inNewTransaction {
            workflowInstanceRepository.nodeError(instanceId, nodeId, message, output)
            workflowInstanceRepository.stopInstance(instanceId)
        }
    }

    override fun findWorkflowInstance(id: String): WorkflowInstance? =
        transactionHelper.inNewTransactionNullable {
            workflowInstanceRepository.findWorkflowInstance(id)
        }

    override fun stopWorkflow(workflowInstanceId: String) {
        transactionHelper.inNewTransaction {
            workflowInstanceRepository.stopInstance(workflowInstanceId)
        }
    }
}
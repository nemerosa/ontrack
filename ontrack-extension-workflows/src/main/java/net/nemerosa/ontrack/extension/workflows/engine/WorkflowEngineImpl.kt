package net.nemerosa.ontrack.extension.workflows.engine

import com.fasterxml.jackson.databind.JsonNode
import kotlinx.coroutines.*
import net.nemerosa.ontrack.extension.workflows.definition.Workflow
import net.nemerosa.ontrack.extension.workflows.definition.WorkflowValidation
import net.nemerosa.ontrack.extension.workflows.execution.WorkflowNodeExecutorResultType
import net.nemerosa.ontrack.extension.workflows.execution.WorkflowNodeExecutorService
import net.nemerosa.ontrack.model.events.SerializableEvent
import net.nemerosa.ontrack.model.tx.TransactionHelper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap

@Service
@Transactional
class WorkflowEngineImpl(
    private val workflowInstanceStore: WorkflowInstanceStore,
    private val workflowNodeExecutorService: WorkflowNodeExecutorService,
    private val transactionHelper: TransactionHelper,
) : WorkflowEngine {

    private val logger: Logger = LoggerFactory.getLogger(WorkflowEngine::class.java)

    private val jobs = ConcurrentHashMap<String, Job>()

    override fun startWorkflow(
        workflow: Workflow,
        event: SerializableEvent,
        callback: (instance: WorkflowInstance) -> Unit,
    ): WorkflowInstance {
        // Checks the workflow consistency (cycles, etc.) - use a public method, usable by extensions
        WorkflowValidation.validateWorkflow(workflow).throwErrorIfAny()
        // Creating the instance
        val instance = createInstance(workflow = workflow, event = event)
        // Storing the instance
        transactionHelper.inNewTransaction {
            workflowInstanceStore.create(instance)
        }
        // Job to run
        logger.debug("{instance=${instance.id}} Creating the job")
        CoroutineScope(Dispatchers.Default).launch {
            // Getting the tasks for all nodes
            val nodeTasks = createNodeTasks(instance)
            // Job to wait on all nodes to complete
            try {
                nodeTasks.awaitAll()
            } finally {
                // Loads the final instance
                val finalInstance = txWorkflowInstance(instance.id)
                // Callback
                callback(finalInstance)
            }
        }.apply {
            // Registering the job
            jobs[instance.id] = this
        }
        logger.debug("{instance=${instance.id}} Started")
        // Returning the instance immediately
        return txWorkflowInstance(instance.id)
    }

    private fun CoroutineScope.createNodeTasks(instance: WorkflowInstance): Collection<Deferred<Unit>> {
        val nodeTasks = mutableMapOf<String, Deferred<Unit>>()
        for (node in instance.workflow.nodes) {
            val nodeTask = async {
                // Logging
                logger.debug("{instance=${instance.id}}{node=${node.id}} Starting")
                // Wait for all parent nodes to complete
                for (parent in node.parents) {
                    logger.debug("{instance=${instance.id}}{node=${node.id}} Waiting for ${parent.id}")
                    val parentTask = nodeTasks[parent.id] ?: error("Parent not found: ${parent.id}")
                    parentTask.await()
                }
                // Checking the parent's status
                if (canRunNode(instance.id, node.id)) {
                    logger.debug("{instance=${instance.id}}{node=${node.id}} Processing")
                    // Process the node
                    processNode(workflowInstanceId = instance.id, workflowNodeId = node.id)
                }
            }
            nodeTasks[node.id] = nodeTask
        }
        return nodeTasks.values
    }

    suspend fun CoroutineScope.processNode(workflowInstanceId: String, workflowNodeId: String) {
        // Getting the instance & the node
        var instance = txWorkflowInstance(workflowInstanceId)
        val node = instance.workflow.getNode(workflowNodeId)
        val instanceNode = instance.getNode(workflowNodeId)
        // Checking the node status
        val nodeStatus = instanceNode.status
        if (nodeStatus == WorkflowInstanceNodeStatus.IDLE) {
            try {
                // Starting the node
                instance = transactionHelper.inNewTransaction {
                    workflowInstanceStore.saveNode(instance, node.id) {
                        it.start()
                    }
                }

                // Getting the node executor
                val executor = workflowNodeExecutorService.getExecutor(node.executorId)
                // Timeout
                val timeout = Duration.ofSeconds(node.timeout)

                // Continuous feedback for the node
                val nodeFeedback: (output: JsonNode?) -> Unit = { output: JsonNode? ->
                    if (output != null) {
                        transactionHelper.inNewTransaction {
                            workflowInstanceStore.saveNode(instance, node.id) {
                                it.progress(output)
                            }
                        }
                    }
                }

                // Running the executor
                val result = withTimeoutOrNull(timeout.toMillis()) {
                    val deferred = async {
                        executor.execute(instance, node.id, nodeFeedback)
                    }
                    deferred.await()
                }

                // Timeout?
                if (result == null) {
                    throw WorkflowExecutionTimeoutException(timeout)
                }

                // Progressing the instance or stopping it in case of error
                when (result.type) {
                    WorkflowNodeExecutorResultType.ERROR -> {
                        transactionHelper.inNewTransaction {
                            workflowInstanceStore.saveNode(
                                instance,
                                node.id
                            ) {
                                it.error(
                                    throwable = null,
                                    message = result.message,
                                    output = result.output,
                                )
                            }
                        }
                        doStopWorkflow(workflowInstanceId)
                    }

                    WorkflowNodeExecutorResultType.SUCCESS -> {
                        // Stores the output back into the instance and progresses the node's status
                        transactionHelper.inNewTransaction {
                            workflowInstanceStore.saveNode(
                                instance,
                                node.id
                            ) {
                                it.success(
                                    output = result.output ?: error("Missing notification output"),
                                )
                            }.run {
                                if (result.event != null) {
                                    workflowInstanceStore.saveEvent(instance, event)
                                } else {
                                    this
                                }
                            }
                        }
                    }
                }
            } catch (any: Throwable) {
                // Stores the node error status
                transactionHelper.inNewTransaction {
                    workflowInstanceStore.saveNode(instance, node.id) {
                        it.error(
                            throwable = any,
                            message = null,
                            output = null,
                        )
                    }
                }
                // Stopping the workflow
                doStopWorkflow(workflowInstanceId)
            }
        } else {
            logger.warn("Node already started, should not be processed. workflowInstanceId=$workflowInstanceId,workflowNodeId=$workflowNodeId")
        }
    }

    private fun canRunNode(instanceId: String, nodeId: String): Boolean {
        // Getting the latest state of the instance
        val instance = txWorkflowInstance(instanceId)
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

    private fun txWorkflowInstance(instanceId: String) = transactionHelper.inNewTransaction {
        workflowInstanceStore.getById(instanceId)
    }

    private fun doStopWorkflow(workflowInstanceId: String) {
        // Getting the instance
        val instance = txWorkflowInstance(workflowInstanceId)
        // Marks the workflow as stopped
        transactionHelper.inNewTransaction {
            workflowInstanceStore.stop(instance)
        }
        // Interrupt the current running tasks
        jobs[workflowInstanceId]?.cancel()
        jobs.remove(workflowInstanceId)
    }

    override fun stopWorkflow(workflowInstanceId: String) {
        doStopWorkflow(workflowInstanceId)
    }

    override fun findWorkflowInstance(id: String): WorkflowInstance? =
        workflowInstanceStore.findById(id)

}
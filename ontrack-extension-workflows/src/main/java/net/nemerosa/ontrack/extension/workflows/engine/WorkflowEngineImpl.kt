package net.nemerosa.ontrack.extension.workflows.engine

import com.fasterxml.jackson.databind.JsonNode
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeoutOrNull
import net.nemerosa.ontrack.extension.queue.dispatching.QueueDispatcher
import net.nemerosa.ontrack.extension.queue.source.createQueueSource
import net.nemerosa.ontrack.extension.workflows.WorkflowConfigurationProperties
import net.nemerosa.ontrack.extension.workflows.definition.Workflow
import net.nemerosa.ontrack.extension.workflows.definition.WorkflowParentNode
import net.nemerosa.ontrack.extension.workflows.definition.WorkflowValidation
import net.nemerosa.ontrack.extension.workflows.definition.totalTimeout
import net.nemerosa.ontrack.extension.workflows.execution.WorkflowNodeExecutorResultType
import net.nemerosa.ontrack.extension.workflows.execution.WorkflowNodeExecutorService
import net.nemerosa.ontrack.extension.workflows.repository.WorkflowInstanceRepository
import net.nemerosa.ontrack.model.events.SerializableEvent
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.templating.TemplatingContextData
import net.nemerosa.ontrack.model.trigger.TriggerData
import net.nemerosa.ontrack.model.tx.DefaultTransactionHelper
import net.nemerosa.ontrack.model.utils.launchAsyncWithSecurityContext
import net.nemerosa.ontrack.model.utils.launchWithSecurityContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.transaction.PlatformTransactionManager
import java.time.Duration
import kotlin.coroutines.CoroutineContext

@Component
class WorkflowEngineImpl(
    private val workflowInstanceRepository: WorkflowInstanceRepository,
    private val queueDispatcher: QueueDispatcher,
    private val workflowQueueProcessor: WorkflowQueueProcessor,
    private val workflowQueueSourceExtension: WorkflowQueueSourceExtension,
    private val workflowNodeExecutorService: WorkflowNodeExecutorService,
    private val workflowConfigurationProperties: WorkflowConfigurationProperties,
    private val securityService: SecurityService,
    platformTransactionManager: PlatformTransactionManager,
) : WorkflowEngine {

    private val logger: Logger = LoggerFactory.getLogger(WorkflowEngineImpl::class.java)

    private val transactionHelper = DefaultTransactionHelper(platformTransactionManager)

    override fun startWorkflow(
        workflow: Workflow,
        event: SerializableEvent,
        triggerData: TriggerData,
        contexts: Map<String, TemplatingContextData>,
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
        val instance = createInstance(
            workflow = actualWorkflow,
            event = event,
            triggerData = triggerData,
            contexts = contexts,
        )

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

    private fun debug(message: String, instance: WorkflowInstance, nodeId: String) {
        logger.debug(
            // WORKFLOW <instance> <name> <node> <user> <message>
            "WORKFLOW [{}] [{}] [{}] [{}] [{}]",
            instance.id,
            instance.workflow.name,
            nodeId,
            SecurityContextHolder.getContext().authentication?.name,
            message
        )
    }

    private fun debugParent(message: String, instance: WorkflowInstance, nodeId: String) {
//        logger.debug(
//            // WORKFLOW <instance> <name> <node> <message>
//            "WORKFLOW [{}] [{}] [{}] [{}]",
//            instance.id,
//            instance.workflow.name,
//            nodeId,
//            message
//        )
    }

    private fun error(message: String, instance: WorkflowInstance, nodeId: String, error: Throwable? = null) {
        val logMessage =
            "[${SecurityContextHolder.getContext().authentication?.name}] WORKFLOW ${instance.workflow.name} INSTANCE ${instance.id} NODE $nodeId: $message"
        if (error != null) {
            logger.error(logMessage, error)
        } else {
            logger.error(logMessage)
        }
    }

    override fun processNode(workflowInstanceId: String, workflowNodeId: String) {
        // Call must be authenticated
        checkAuthentication("Workflow node received not authenticated")
        // Getting the instance
        val instance = getWorkflowInstanceTx(workflowInstanceId)
        val node = instance.workflow.getNode(workflowNodeId)
        val nodeExecution = instance.getNode(workflowNodeId)
        debug("NODE RECEIVED", instance, workflowNodeId)
        // Checks of the instance has been interrupted or not
        if (instance.status.finished) {
            debug("NODE INSTANCE FINISHED", instance, workflowNodeId)
            return
        }
        // Checks the instance node status
        if (nodeExecution.status.finished) {
            debug("NODE FINISHED", instance, workflowNodeId)
            return
        } else if (nodeExecution.status != WorkflowInstanceNodeStatus.CREATED) {
            error("NODE NOT IN CREATED STATE", instance, workflowNodeId)
            return
        }
        // Starting the job for the node
        launchWithSecurityContext {
            // Call must be authenticated
            checkAuthentication("Workflow node process not authenticated")
            debug("NODE PROCESSING", instance, workflowNodeId)
            // Changes the node's status to WAITING
            debug("NODE WAITING", instance, workflowNodeId)
            nodeWaiting(workflowInstanceId, workflowNodeId)
            // Waiting for the parent nodes to be OK
            val okToStart = if (node.parents.isNotEmpty()) {
                val securityContext = SecurityContextHolder.getContext()
                val parentStatuses = try {
                    val parentJobs = node.parents.map {
                        createParentJob(
                            coroutineContext = coroutineContext,
                            instance = instance,
                            workflowNodeId = workflowNodeId,
                            parentDef = it
                        )
                    }
                    debug("NODE WAITING FOR PARENTS", instance, workflowNodeId)
                    parentJobs.awaitAll()
                } finally {
                    SecurityContextHolder.setContext(securityContext)
                }
                debug("NODE WAITED FOR PARENTS", instance, workflowNodeId)
                // TODO OK to start depending on the conditions
                val parentsOK = parentStatuses.all { it.status == WorkflowInstanceNodeStatus.SUCCESS }
                // Checking the instance state again
                if (parentsOK) {
                    !getWorkflowInstanceTx(workflowInstanceId).status.finished
                } else {
                    val statusSummary = parentStatuses.joinToString(",") {
                        "${it.parentDef.id}=${it.status}"
                    }
                    debugParent("NODE PARENTS STATUSES: $statusSummary", instance, workflowNodeId)
                    false
                }
            } else {
                // No parent
                true
            }
            // Starting the node execution
            if (okToStart) {
                debug("NODE STARTED", instance, workflowNodeId)
                nodeStarted(workflowInstanceId, workflowNodeId)
                // Loading a fresh instance before starting
                val freshInstance = getWorkflowInstanceTx(workflowInstanceId)
                // Starts the node execution
                nodeExecution(freshInstance, workflowNodeId)
            } else {
                debug("NODE PARENT NOT OK", instance, workflowNodeId)
                nodeCancelled(workflowInstanceId, workflowNodeId, "Parents conditions were not met.")
            }
        }
        debug("NODE LAUNCHED", instance, workflowNodeId)
    }

    private fun getWorkflowInstanceTx(workflowInstanceId: String) = transactionHelper.inNewTransaction {
        workflowInstanceRepository.findWorkflowInstance(workflowInstanceId)
            ?: error("Could not find the workflow instance: $workflowInstanceId")
    }

    private fun createParentJob(
        coroutineContext: CoroutineContext,
        instance: WorkflowInstance,
        workflowNodeId: String,
        parentDef: WorkflowParentNode
    ): Deferred<WorkflowParentStatus> {
        val parentNode = instance.workflow.getNode(parentDef.id)
        val parentTimeoutSeconds = parentNode.totalTimeout(instance.workflow)
        val parentTimeoutMs = parentTimeoutSeconds * 1_000L
        return launchAsyncWithSecurityContext(context = coroutineContext) {
            debugParent("NODE PARENT WAIT ${parentDef.id} START", instance, workflowNodeId)
            val status = withTimeoutOrNull(parentTimeoutMs) {
                var parentFinished = false
                var parentStatus: WorkflowInstanceNodeStatus? = null
                while (!parentFinished) {
                    debugParent("NODE PARENT WAIT ${parentDef.id} STATUS", instance, workflowNodeId)
                    parentStatus = getNodeStatus(instance.id, parentDef.id)
                        ?: error("Could not find status for parent node: ${instance.id} / ${parentDef.id}")
                    if (parentStatus.finished) {
                        parentFinished = true
                    } else {
                        debugParent("NODE PARENT WAIT ${parentDef.id} DELAY", instance, workflowNodeId)
                        delay(workflowConfigurationProperties.parentWaitingInterval.toMillis())
                    }
                }
                debugParent("NODE PARENT WAIT ${parentDef.id} DONE $parentStatus", instance, workflowNodeId)
                parentStatus
            }
            WorkflowParentStatus(parentDef, status ?: WorkflowInstanceNodeStatus.TIMEOUT)
        }
    }

    private suspend fun nodeExecution(instance: WorkflowInstance, nodeId: String) {
        // Getting the node
        val node = instance.workflow.getNode(nodeId)
        // Getting the node executor
        val executor = workflowNodeExecutorService.getExecutor(node.executorId)
        // Checking if the executor is enabled
        if (!executor.enabled) {
            throw WorkflowNodeExecutorNotEnabledException(executor)
        }
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
                val deferred = launchAsyncWithSecurityContext(context = coroutineContext) {
                    debug("NODE EXECUTING", instance, nodeId)
                    // Call must be authenticated
                    checkAuthentication("Workflow node execution not authenticated")
                    // Running the execution
                    executor.execute(instance, node.id, nodeFeedback)
                }
                val outcome = deferred.await()
                debug("NODE EXECUTED (type = ${outcome.type})", instance, nodeId)
                outcome
            }

            // Timeout?
            if (result == null) {
                debug("NODE TIMEOUT", instance, nodeId)
                nodeError(instance.id, nodeId, "Timeout", null)
            }
            // Progressing the instance or stopping it in case of error
            else {
                when (result.type) {
                    WorkflowNodeExecutorResultType.ERROR -> {
                        debug("NODE ERROR (message = ${result.message})", instance, nodeId)
                        nodeError(instance.id, nodeId, result.message, result.output)
                    }

                    WorkflowNodeExecutorResultType.SUCCESS -> {
                        debug("NODE SUCCESS", instance, nodeId)
                        // Stores the output back into the instance and progresses the node's status
                        nodeSuccess(instance.id, nodeId, result.output, result.event)
                    }
                }
            }
        } catch (any: Throwable) {
            // Stores the node error status
            error("NODE UNCAUGHT ERROR", instance, nodeId, any)
            nodeError(instance.id, nodeId, any.message, null)
        }
    }

    private fun checkAuthentication(message: String) {
        securityService.currentUser ?: error(message)
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

    private fun nodeCancelled(
        workflowInstanceId: String,
        workflowNodeId: String,
        @Suppress("SameParameterValue") message: String
    ) {
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
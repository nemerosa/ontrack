package net.nemerosa.ontrack.extension.workflows.engine.parallel

import net.nemerosa.ontrack.extension.queue.QueueProcessor
import net.nemerosa.ontrack.extension.workflows.engine.WorkflowEngine
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component
import kotlin.reflect.KClass

@Component
class ParallelWorkflowQueueProcessor(
    private val applicationContext: ApplicationContext,
) : QueueProcessor<WorkflowQueuePayload> {

    private val workflowEngine: WorkflowEngine by lazy {
        applicationContext.getBean(WorkflowEngine::class.java)
    }

    override val id: String = "workflow-parallel"
    override val payloadType: KClass<WorkflowQueuePayload> = WorkflowQueuePayload::class

    override fun isCancelled(payload: WorkflowQueuePayload): String? = null

    override fun process(payload: WorkflowQueuePayload) {
        workflowEngine.processNode(
            workflowInstanceId = payload.workflowInstanceId,
            workflowNodeId = payload.workflowNodeId,
        )
    }

    override fun getRoutingIdentifier(payload: WorkflowQueuePayload): String =
        "${payload.workflowInstanceId}-${payload.workflowNodeId}"

}
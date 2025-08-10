package net.nemerosa.ontrack.extension.workflows.engine

import net.nemerosa.ontrack.extension.queue.QueueAckMode
import net.nemerosa.ontrack.extension.queue.QueueMetadata
import net.nemerosa.ontrack.extension.queue.QueueProcessor
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component
import kotlin.reflect.KClass

@Component
class WorkflowQueueProcessor(
    private val applicationContext: ApplicationContext,
) : QueueProcessor<WorkflowQueuePayload> {

    private val workflowEngine: WorkflowEngine by lazy {
        applicationContext.getBean(WorkflowEngine::class.java)
    }

    override val id: String = "workflow"
    override val payloadType: KClass<WorkflowQueuePayload> = WorkflowQueuePayload::class

    /**
     * Minimum of 5 queues for the workflows.
     */
    override val defaultScale: Int = 5

    /**
     * Immediately ack the messages
     */
    override val ackMode: QueueAckMode = QueueAckMode.IMMEDIATE

    override fun isCancelled(payload: WorkflowQueuePayload): String? = null

    override fun process(payload: WorkflowQueuePayload, queueMetadata: QueueMetadata?) {
        workflowEngine.processNode(
            workflowInstanceId = payload.workflowInstanceId,
            workflowNodeId = payload.workflowNodeId,
        )
    }

    override fun getRoutingIdentifier(payload: WorkflowQueuePayload): String =
        "${payload.workflowInstanceId}-${payload.workflowNodeId}"

}
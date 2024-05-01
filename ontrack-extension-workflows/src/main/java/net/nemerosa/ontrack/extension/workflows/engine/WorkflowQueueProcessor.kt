package net.nemerosa.ontrack.extension.workflows.engine

import net.nemerosa.ontrack.extension.queue.QueueProcessor
import org.springframework.stereotype.Component
import kotlin.reflect.KClass

@Component
class WorkflowQueueProcessor : QueueProcessor<WorkflowQueuePayload> {

    override val id: String = "workflows"

    override val payloadType: KClass<WorkflowQueuePayload> = WorkflowQueuePayload::class

    override fun isCancelled(payload: WorkflowQueuePayload): String? = null

    override fun process(payload: WorkflowQueuePayload) {
        TODO("Not yet implemented")
    }

    override fun getRoutingIdentifier(payload: WorkflowQueuePayload): String = payload.workflowNodeExecutorId
}
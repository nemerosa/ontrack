package net.nemerosa.ontrack.extension.workflows.notifications

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.notifications.model.Notification
import net.nemerosa.ontrack.extension.notifications.processing.NotificationProcessingService
import net.nemerosa.ontrack.extension.notifications.queue.NotificationQueueItem
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.extension.workflows.WorkflowsExtensionFeature
import net.nemerosa.ontrack.extension.workflows.engine.WorkflowInstance
import net.nemerosa.ontrack.extension.workflows.execution.WorkflowNodeExecutor
import net.nemerosa.ontrack.extension.workflows.templating.WorkflowInfoTemplatingRenderable
import net.nemerosa.ontrack.extension.workflows.templating.WorkflowTemplatingRenderable
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.docs.Documentation
import net.nemerosa.ontrack.model.docs.DocumentationExampleCode
import net.nemerosa.ontrack.model.events.Event
import org.springframework.stereotype.Component

@Component
@APIDescription(
    """
    Wraps a notification in a workflow node.
    
    The output of this execution is exactly the output
    of the notification channel.
"""
)
@DocumentationExampleCode(
    """
        executorId: notification
        data:
            channel: slack
            channelConfig:
                channel: "#my-channel"
            template: |
                Message template
    """
)
@Documentation(WorkflowNotificationChannelNodeData::class)
class WorkflowNotificationChannelNodeExecutor(
    workflowsExtensionFeature: WorkflowsExtensionFeature,
    private val notificationProcessingService: NotificationProcessingService,
    private val workflowNotificationItemConverter: WorkflowNotificationItemConverter,
) : AbstractExtension(workflowsExtensionFeature), WorkflowNodeExecutor {

    companion object {
        const val ID: String = "notification"

        const val CONTEXT_EVENT = "event"
    }

    override val id: String = ID
    override val displayName: String = "Notification"

    override suspend fun execute(workflowInstance: WorkflowInstance, workflowNodeId: String): JsonNode {
        // Gets the node's data
        val (channel, channelConfig, template) = workflowInstance.workflow.getNode(workflowNodeId).data.parse<WorkflowNotificationChannelNodeData>()
        // Gets the context
        val queueItem = workflowInstance.context.parse<NotificationQueueItem>(CONTEXT_EVENT)
        // Creating the notification item
        val notification = workflowNotificationItemConverter.convertFromQueue(
            channel = channel,
            channelConfig = channelConfig,
            template = template,
            queueItem = queueItem
        )
        // Enriches the context
        val context = mapOf(
            "workflow" to WorkflowTemplatingRenderable(workflowInstance),
            "workflowInfo" to WorkflowInfoTemplatingRenderable(workflowInstance),
        )
        // Processing
        val output = notificationProcessingService.process(notification, context)
        // Returning the output
        return output.asJson()
    }
}
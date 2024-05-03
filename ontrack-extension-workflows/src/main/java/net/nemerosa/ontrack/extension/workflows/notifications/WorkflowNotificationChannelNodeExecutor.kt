package net.nemerosa.ontrack.extension.workflows.notifications

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.notifications.model.Notification
import net.nemerosa.ontrack.extension.notifications.processing.NotificationProcessingService
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.extension.workflows.WorkflowsExtensionFeature
import net.nemerosa.ontrack.extension.workflows.engine.WorkflowInstance
import net.nemerosa.ontrack.extension.workflows.execution.WorkflowNodeExecutor
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.model.events.Event
import org.springframework.stereotype.Component

@Component
class WorkflowNotificationChannelNodeExecutor(
    workflowsExtensionFeature: WorkflowsExtensionFeature,
    private val notificationProcessingService: NotificationProcessingService,
) : AbstractExtension(workflowsExtensionFeature), WorkflowNodeExecutor {

    companion object {
        const val ID: String = "workflow-notification"
    }

    override val id: String = ID

    override fun execute(workflowInstance: WorkflowInstance, workflowNodeId: String): JsonNode {
        // Gets the node's data
        val (channel, channelConfig, template) = workflowInstance.workflow.getNode(workflowNodeId).data.parse<WorkflowNotificationChannelNodeData>()
        // Gets the context
        val event = workflowInstance.context.parse<Event>()
        // Creating the notification item
        val item = Notification(
            channel = channel,
            channelConfig = channelConfig,
            event = event,
            template = template,
        )
        // Processing
        val output = notificationProcessingService.process(item)
        // Returning the output
        return output.asJson()
    }
}
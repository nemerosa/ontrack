package net.nemerosa.ontrack.extension.workflows.notifications

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.notifications.channels.NotificationResultType
import net.nemerosa.ontrack.extension.notifications.processing.NotificationProcessingService
import net.nemerosa.ontrack.extension.notifications.queue.NotificationQueueItem
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.extension.workflows.WorkflowsExtensionFeature
import net.nemerosa.ontrack.extension.workflows.engine.WorkflowInstance
import net.nemerosa.ontrack.extension.workflows.execution.WorkflowNodeExecutor
import net.nemerosa.ontrack.extension.workflows.execution.WorkflowNodeExecutorResult
import net.nemerosa.ontrack.extension.workflows.templating.WorkflowInfoTemplatingRenderable
import net.nemerosa.ontrack.extension.workflows.templating.WorkflowTemplatingRenderable
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.docs.Documentation
import net.nemerosa.ontrack.model.docs.DocumentationExampleCode
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

    override suspend fun execute(
        workflowInstance: WorkflowInstance,
        workflowNodeId: String,
        workflowNodeExecutorResultFeedback: (output: JsonNode?) -> Unit,
    ): WorkflowNodeExecutorResult {
        // Gets the node's data
        val (channel, channelConfig, template) = workflowInstance.workflow.getNode(workflowNodeId).data.parse<WorkflowNotificationChannelNodeData>()
        // Gets the context
        val queueItem = workflowInstance.context.parse<NotificationQueueItem>(CONTEXT_EVENT)
        // Creating the notification item
        val notification = workflowNotificationItemConverter.convertFromQueue(
            instanceId = workflowInstance.id,
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
        // Feedback
        val outputFeedback = { output: Any? ->
            workflowNodeExecutorResultFeedback(output?.asJson())
        }
        // Processing
        val result = notificationProcessingService.process(notification, context, outputFeedback)
        // Result of the execution
        return if (result != null) {
            when (result.type) {
                NotificationResultType.OK -> WorkflowNodeExecutorResult.success(result.output?.asJson())
                NotificationResultType.ASYNC -> WorkflowNodeExecutorResult.success(result.output?.asJson())
                NotificationResultType.ONGOING -> WorkflowNodeExecutorResult.error(
                    "Notification is still ongoing",
                    result.output?.asJson()
                )

                NotificationResultType.NOT_CONFIGURED -> WorkflowNodeExecutorResult.error(
                    "Notification is not configured",
                    result.output?.asJson()
                )

                NotificationResultType.INVALID_CONFIGURATION -> WorkflowNodeExecutorResult.error(
                    "Notification configuration is invalid",
                    result.output?.asJson()
                )

                NotificationResultType.DISABLED -> WorkflowNodeExecutorResult.error(
                    "Notification is disabled",
                    result.output?.asJson()
                )

                NotificationResultType.ERROR -> WorkflowNodeExecutorResult.error(
                    result.message ?: "Unknown error",
                    result.output?.asJson()
                )
            }
        } else {
            WorkflowNodeExecutorResult.error("Notification did not return any result", null)
        }
    }
}
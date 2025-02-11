package net.nemerosa.ontrack.extension.workflows.notifications

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.notifications.channels.NotificationChannelRegistry
import net.nemerosa.ontrack.extension.notifications.channels.NotificationResultType
import net.nemerosa.ontrack.extension.notifications.channels.getChannel
import net.nemerosa.ontrack.extension.notifications.channels.throwException
import net.nemerosa.ontrack.extension.notifications.model.Notification
import net.nemerosa.ontrack.extension.notifications.model.createData
import net.nemerosa.ontrack.extension.notifications.processing.NotificationProcessingService
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.extension.workflows.WorkflowsExtensionFeature
import net.nemerosa.ontrack.extension.workflows.engine.WorkflowInstance
import net.nemerosa.ontrack.extension.workflows.execution.WorkflowNodeExecutor
import net.nemerosa.ontrack.extension.workflows.execution.WorkflowNodeExecutorResult
import net.nemerosa.ontrack.extension.workflows.templating.WorkflowTemplatingContext
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.docs.Documentation
import net.nemerosa.ontrack.model.docs.DocumentationExampleCode
import net.nemerosa.ontrack.model.events.SerializableEventService
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
    private val notificationChannelRegistry: NotificationChannelRegistry,
    private val workflowNotificationSource: WorkflowNotificationSource,
    private val serializableEventService: SerializableEventService,
) : AbstractExtension(workflowsExtensionFeature), WorkflowNodeExecutor {

    companion object {
        const val ID: String = "notification"
    }

    override val id: String = ID
    override val displayName: String = "Notification"

    override fun validate(data: JsonNode) {
        val (channelType, channelConfig) = data.parse<WorkflowNotificationChannelNodeData>()
        val channel = notificationChannelRegistry.getChannel(channelType)
        val validation = channel.validate(channelConfig)
        if (!validation.isOk()) {
            validation.throwException()
        }
    }

    override fun execute(
        workflowInstance: WorkflowInstance,
        workflowNodeId: String,
        workflowNodeExecutorResultFeedback: (output: JsonNode?) -> Unit,
    ): WorkflowNodeExecutorResult {
        // Gets the node's data
        val (channel, channelConfig, template) = workflowInstance.workflow.getNode(workflowNodeId).data.parse<WorkflowNotificationChannelNodeData>()
        // Creating the notification item
        val notification = Notification(
            source = workflowNotificationSource.createData(
                WorkflowNotificationSourceDataType(
                    workflowInstanceId = workflowInstance.id,
                )
            ),
            channel = channel,
            channelConfig = channelConfig,
            event = serializableEventService.hydrate(workflowInstance.event),
            template = template,
        )
        // Enriches the context
        val context = WorkflowTemplatingContext.createTemplatingContext(workflowInstance)
        // Feedback
        val outputFeedback = { output: Any? ->
            workflowNodeExecutorResultFeedback(output?.asJson())
        }
        // Processing
        val processingResult = notificationProcessingService.process(notification, context, outputFeedback)
        val result = processingResult?.result
        // Result of the execution
        return if (processingResult != null && result != null) {
            when (result.type) {
                NotificationResultType.OK -> WorkflowNotificationChannelNodeExecutorOutput.success(processingResult)
                NotificationResultType.ASYNC -> WorkflowNotificationChannelNodeExecutorOutput.success(processingResult)
                NotificationResultType.ONGOING -> WorkflowNotificationChannelNodeExecutorOutput.error(
                    "Notification is still ongoing",
                    processingResult,
                )

                NotificationResultType.NOT_CONFIGURED -> WorkflowNotificationChannelNodeExecutorOutput.error(
                    "Notification is not configured",
                    processingResult
                )

                NotificationResultType.INVALID_CONFIGURATION -> WorkflowNotificationChannelNodeExecutorOutput.error(
                    "Notification configuration is invalid",
                    processingResult
                )

                NotificationResultType.DISABLED -> WorkflowNotificationChannelNodeExecutorOutput.error(
                    "Notification is disabled",
                    processingResult
                )

                NotificationResultType.ERROR -> WorkflowNotificationChannelNodeExecutorOutput.error(
                    result.message ?: "Unknown error",
                    processingResult
                )

                NotificationResultType.TIMEOUT -> WorkflowNotificationChannelNodeExecutorOutput.error(
                    "Timeout when running a workflow",
                    processingResult
                )
            }
        } else {
            WorkflowNodeExecutorResult.error("Notification did not return any result", null)
        }
    }
}
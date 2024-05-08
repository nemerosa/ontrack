package net.nemerosa.ontrack.extension.workflows.notifications

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.NullNode
import net.nemerosa.ontrack.extension.notifications.channels.AbstractNotificationChannel
import net.nemerosa.ontrack.extension.notifications.channels.NoTemplate
import net.nemerosa.ontrack.extension.notifications.channels.NotificationResult
import net.nemerosa.ontrack.extension.notifications.model.Notification
import net.nemerosa.ontrack.extension.notifications.queue.NotificationQueueItemConverter
import net.nemerosa.ontrack.extension.workflows.definition.Workflow
import net.nemerosa.ontrack.extension.workflows.definition.WorkflowNode
import net.nemerosa.ontrack.extension.workflows.engine.WorkflowContext
import net.nemerosa.ontrack.extension.workflows.engine.WorkflowEngine
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.docs.Documentation
import net.nemerosa.ontrack.model.events.Event
import net.nemerosa.ontrack.model.form.Form
import org.springframework.stereotype.Component

@Component
@APIDescription("Launches a workflow")
@NoTemplate
@Documentation(WorkflowNotificationChannelConfig::class)
@Documentation(WorkflowNotificationChannelOutput::class, section = "output")
class WorkflowNotificationChannel(
    private val workflowEngine: WorkflowEngine,
    private val workflowNotificationItemConverter: WorkflowNotificationItemConverter,
) : AbstractNotificationChannel<WorkflowNotificationChannelConfig, WorkflowNotificationChannelOutput>(
    WorkflowNotificationChannelConfig::class
) {

    override fun publish(
        config: WorkflowNotificationChannelConfig,
        event: Event,
        context: Map<String, Any>,
        template: String?, // Not used for this notification channel
        outputProgressCallback: (current: WorkflowNotificationChannelOutput) -> WorkflowNotificationChannelOutput
    ): NotificationResult<WorkflowNotificationChannelOutput> {
        // Converting the event to a suitable format
        val item = workflowNotificationItemConverter.convertForQueue(event)
        // Launching the workflow (with the event as context, template is not used)
        val instance = workflowEngine.startWorkflow(
            workflow = config.workflow,
            context = WorkflowContext(WorkflowNotificationChannelNodeExecutor.CONTEXT_EVENT, item.asJson()),
        )
        // Output contains only the instance ID
        return NotificationResult.ok(
            WorkflowNotificationChannelOutput(
                workflowInstanceId = instance.id,
            )
        )
    }

    override fun toSearchCriteria(text: String): JsonNode {
        TODO("Not yet implemented")
    }

    override val type: String = "workflow"
    override val displayName: String = "Workflow"
    override val enabled: Boolean = true

    @Deprecated("Will be removed in V5. Only Next UI is used.")
    override fun getForm(c: WorkflowNotificationChannelConfig?): Form {
        TODO("Not yet implemented")
    }

    @Deprecated("Will be removed in V5. Only Next UI is used.")
    override fun toText(config: WorkflowNotificationChannelConfig): String {
        TODO("Not yet implemented")
    }
}
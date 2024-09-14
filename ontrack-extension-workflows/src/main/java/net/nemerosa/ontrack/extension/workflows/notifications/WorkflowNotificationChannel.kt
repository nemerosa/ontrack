package net.nemerosa.ontrack.extension.workflows.notifications

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.notifications.channels.AbstractNotificationChannel
import net.nemerosa.ontrack.extension.notifications.channels.NoTemplate
import net.nemerosa.ontrack.extension.notifications.channels.NotificationResult
import net.nemerosa.ontrack.extension.workflows.definition.totalTimeout
import net.nemerosa.ontrack.extension.workflows.engine.WorkflowContext
import net.nemerosa.ontrack.extension.workflows.engine.WorkflowEngine
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.docs.Documentation
import net.nemerosa.ontrack.model.events.Event
import net.nemerosa.ontrack.model.events.EventTemplatingService
import net.nemerosa.ontrack.model.events.PlainEventRenderer
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
    private val eventTemplatingService: EventTemplatingService,
) : AbstractNotificationChannel<WorkflowNotificationChannelConfig, WorkflowNotificationChannelOutput>(
    WorkflowNotificationChannelConfig::class
) {

    override fun publish(
        recordId: String,
        config: WorkflowNotificationChannelConfig,
        event: Event,
        context: Map<String, Any>,
        template: String?, // Not used for this notification channel
        outputProgressCallback: (current: WorkflowNotificationChannelOutput) -> WorkflowNotificationChannelOutput
    ): NotificationResult<WorkflowNotificationChannelOutput> {
        // Templating for the workflow name
        val workflow = config.workflow.rename {
            eventTemplatingService.renderEvent(
                event = event,
                context = context,
                template = it,
                renderer = PlainEventRenderer.INSTANCE,
            )
        }
        // Total timeout
        val workflowTimeout = workflow.totalTimeout
        // Launching the workflow (with the event as context, template is not used)
        val instance = workflowEngine.startWorkflow(
            workflow = workflow,
            context = WorkflowContext.noContext(),
        ) { ctx, instanceId ->
            // Converting the event to a suitable format
            val item = workflowNotificationItemConverter.convertForQueue(event, instanceId)
            // Adding to the context
            ctx
                .withData(
                    WorkflowNotificationChannelNodeExecutor.CONTEXT_EVENT,
                    item.asJson()
                )
                .withData(
                    WorkflowNotificationChannelNotificationRecord.CONTEXT_NOTIFICATION_RECORD_ID,
                    WorkflowNotificationChannelNotificationRecord(recordId).asJson()
                )
        }
        // Output contains only the instance ID
        return NotificationResult.ok(
            WorkflowNotificationChannelOutput(
                workflowInstanceId = instance.id,
            )
        )
    }

    override fun toSearchCriteria(text: String): JsonNode = mapOf(
        "workflow" to mapOf(
            "name" to text
        )
    ).asJson()

    override val type: String = "workflow"
    override val displayName: String = "Workflow"
    override val enabled: Boolean = true

    @Deprecated("Will be removed in V5. Only Next UI is used.")
    override fun getForm(c: WorkflowNotificationChannelConfig?): Form = Form.create()

    @Deprecated("Will be removed in V5. Only Next UI is used.")
    override fun toText(config: WorkflowNotificationChannelConfig): String = config.workflow.name
}
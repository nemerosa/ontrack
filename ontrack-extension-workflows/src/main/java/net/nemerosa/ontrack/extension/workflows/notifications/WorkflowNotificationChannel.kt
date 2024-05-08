package net.nemerosa.ontrack.extension.workflows.notifications

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.notifications.channels.AbstractNotificationChannel
import net.nemerosa.ontrack.extension.notifications.channels.NotificationResult
import net.nemerosa.ontrack.extension.workflows.definition.Workflow
import net.nemerosa.ontrack.extension.workflows.definition.WorkflowNode
import net.nemerosa.ontrack.extension.workflows.engine.WorkflowContext
import net.nemerosa.ontrack.extension.workflows.engine.WorkflowEngine
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.model.events.Event
import net.nemerosa.ontrack.model.form.Form
import org.springframework.stereotype.Component

@Component
class WorkflowNotificationChannel(
    private val workflowEngine: WorkflowEngine,
) : AbstractNotificationChannel<WorkflowNotificationChannelConfig, WorkflowNotificationChannelOutput>(
    WorkflowNotificationChannelConfig::class
) {

    override fun publish(
        config: WorkflowNotificationChannelConfig,
        event: Event,
        template: String?,
        outputProgressCallback: (current: WorkflowNotificationChannelOutput) -> WorkflowNotificationChannelOutput
    ): NotificationResult<WorkflowNotificationChannelOutput> {
        // Launching the workflow (with the event as context, template is not used)
        val instance = workflowEngine.startWorkflow(
            workflow = config.workflow,
            context = WorkflowContext("event", event.asJson()),
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
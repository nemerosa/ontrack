package net.nemerosa.ontrack.extension.environments.notifications

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.NullNode
import net.nemerosa.ontrack.extension.environments.events.EnvironmentsEvents
import net.nemerosa.ontrack.extension.environments.service.EnvironmentService
import net.nemerosa.ontrack.extension.environments.service.SlotService
import net.nemerosa.ontrack.extension.environments.service.getPipelineById
import net.nemerosa.ontrack.extension.notifications.channels.AbstractNotificationChannel
import net.nemerosa.ontrack.extension.notifications.channels.NotificationResult
import net.nemerosa.ontrack.model.events.Event
import net.nemerosa.ontrack.model.form.Form
import org.springframework.stereotype.Component

@Component
class SlotPipelineCreationNotificationChannel(
    private val slotService: SlotService,
    private val environmentService: EnvironmentService,
) : AbstractNotificationChannel<SlotPipelineCreationNotificationChannelConfig, SlotPipelineCreationNotificationChannelOutput>(
    configClass = SlotPipelineCreationNotificationChannelConfig::class
) {

    override fun publish(
        recordId: String,
        config: SlotPipelineCreationNotificationChannelConfig,
        event: Event,
        context: Map<String, Any>,
        template: String?,
        outputProgressCallback: (current: SlotPipelineCreationNotificationChannelOutput) -> SlotPipelineCreationNotificationChannelOutput
    ): NotificationResult<SlotPipelineCreationNotificationChannelOutput> {
        // Getting the pipeline ID
        val pipelineId = event.getValue(EnvironmentsEvents.EVENT_PIPELINE_ID)
        // Getting the pipeline
        val pipeline = slotService.getPipelineById(pipelineId)
        // Getting the target environment
        val targetEnvironment = environmentService.findByName(config.environment)
            ?: return NotificationResult.error("Environment name not found: ${config.environment}")
        // Finding the target slot for the same project
        val qualifier = config.qualifier ?: pipeline.slot.qualifier
        val targetSlot = slotService.findSlotsByEnvironment(targetEnvironment).find {
            it.project.id() == pipeline.slot.project.id() && it.qualifier == qualifier
        }
            ?: return NotificationResult.error("Cannot find slot for environment = ${config.environment}, project = ${pipeline.slot.project.name}, qualifier = $qualifier")
        // Is the build eligible for the target slot?
        if (!slotService.isBuildEligible(targetSlot, pipeline.build)) {
            return NotificationResult.error("Build is not eligible for target slot")
        }
        // Creating the pipeline
        val targetPipeline = slotService.startPipeline(targetSlot, pipeline.build)
        // OK
        return NotificationResult.ok(
            SlotPipelineCreationNotificationChannelOutput(
                pipelineId = targetPipeline.id,
            )
        )
    }

    override fun toSearchCriteria(text: String): JsonNode = NullNode.instance

    override val type: String = "slot-pipeline-creation"
    override val displayName: String = "Slot pipeline creation"
    override val enabled: Boolean = true

    @Deprecated(
        "Will be removed in V5. Only Next UI is used.",
        ReplaceWith("Form.create()", "net.nemerosa.ontrack.model.form.Form")
    )
    override fun getForm(c: SlotPipelineCreationNotificationChannelConfig?): Form = Form.create()

    @Deprecated("Will be removed in V5. Only Next UI is used.", ReplaceWith("\"\""))
    override fun toText(config: SlotPipelineCreationNotificationChannelConfig): String = ""
}
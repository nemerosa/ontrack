package net.nemerosa.ontrack.extension.environments.workflows.executors

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.environments.EnvironmentsExtensionFeature
import net.nemerosa.ontrack.extension.environments.SlotPipeline
import net.nemerosa.ontrack.extension.environments.service.SlotService
import net.nemerosa.ontrack.extension.environments.service.getPipelineById
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.extension.workflows.engine.WorkflowInstance
import net.nemerosa.ontrack.extension.workflows.execution.WorkflowNodeExecutor
import net.nemerosa.ontrack.extension.workflows.execution.WorkflowNodeExecutorResult
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.model.events.SerializableEvent
import net.nemerosa.ontrack.model.security.SecurityService
import org.springframework.stereotype.Component

@Component
class SlotPipelineDeployingWorkflowNodeExecutor(
    extensionFeature: EnvironmentsExtensionFeature,
    private val slotService: SlotService,
    private val securityService: SecurityService,
) : AbstractExtension(extensionFeature), WorkflowNodeExecutor {

    override val id: String = "slot-pipeline-deploying"
    override val displayName: String = "Deploying pipeline"

    override fun validate(data: JsonNode) {}

    override fun execute(
        workflowInstance: WorkflowInstance,
        workflowNodeId: String,
        workflowNodeExecutorResultFeedback: (output: JsonNode?) -> Unit
    ): WorkflowNodeExecutorResult {
        return securityService.asAdmin {
            // Getting the pipeline from the context
            val pipeline = getPipelineFromContext(workflowInstance.event)
            // Getting the slot workflow
            val slotWorkflowId = workflowInstance.event.findSlotWorkflowId()
            // Progressing the pipeline
            val status = slotService.runDeployment(
                pipelineId = pipeline.id,
                dryRun = false,
                // Skipping the check on its own workflow
                skipWorkflowId = slotWorkflowId,
            )
            // Deployment started
            val result = if (status.ok) {
                WorkflowNodeExecutorResult.success(
                    SlotPipelineDeployingWorkflowNodeExecutorOutput(
                        pipelineId = pipeline.id,
                    ).asJson()
                )
            } else {
                WorkflowNodeExecutorResult.error(
                    "Pipeline conditions were not met to start the deployment.",
                    SlotPipelineDeployingWorkflowNodeExecutorOutput(
                        pipelineId = pipeline.id,
                    ).asJson()
                )
            }
            // OK
            result
        }
    }

    private fun getPipelineFromContext(serializableEvent: SerializableEvent): SlotPipeline =
        serializableEvent.findValue(SlotWorkflowContext.EVENT_SLOT_PIPELINE_ID)
            ?.let {
                slotService.getPipelineById(it)
            }
            ?: error("Cannot find any pipeline in the workflow context")

}
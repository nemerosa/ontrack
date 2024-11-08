package net.nemerosa.ontrack.extension.environments.workflows

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.environments.EnvironmentsExtensionFeature
import net.nemerosa.ontrack.extension.environments.service.EnvironmentService
import net.nemerosa.ontrack.extension.environments.service.SlotService
import net.nemerosa.ontrack.extension.environments.service.getPipelineById
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.extension.workflows.engine.WorkflowInstance
import net.nemerosa.ontrack.extension.workflows.execution.WorkflowNodeExecutor
import net.nemerosa.ontrack.extension.workflows.execution.WorkflowNodeExecutorResult
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parse
import org.springframework.stereotype.Component

@Component
class SlotPipelineCreationWorkflowNodeExecutor(
    extensionFeature: EnvironmentsExtensionFeature,
    private val slotService: SlotService,
    private val environmentService: EnvironmentService,
) : AbstractExtension(extensionFeature), WorkflowNodeExecutor {

    override val id: String = "slot-pipeline-creation"
    override val displayName: String = "Pipeline creation"

    override suspend fun execute(
        workflowInstance: WorkflowInstance,
        workflowNodeId: String,
        workflowNodeExecutorResultFeedback: (output: JsonNode?) -> Unit
    ): WorkflowNodeExecutorResult {
        val (environment, qualifier) = workflowInstance.workflow.getNode(workflowNodeId).data.parse<SlotPipelineCreationWorkflowNodeExecutorData>()
        val (pipelineId, _) = workflowInstance.context
            .parse<SlotPipelineWorkflowContext>(SlotPipelineWorkflowContext.CONTEXT)

        // Getting the pipeline
        val pipeline = slotService.getPipelineById(pipelineId)
        // Getting the target environment
        val targetEnvironment = environmentService.findByName(environment)
            ?: return WorkflowNodeExecutorResult.error("Environment name not found: ${environment}", output = null)
        // Finding the target slot for the same project
        val actualQualifier = qualifier ?: pipeline.slot.qualifier
        val targetSlot = slotService.findSlotsByEnvironment(targetEnvironment).find {
            it.project.id() == pipeline.slot.project.id() && it.qualifier == actualQualifier
        }
            ?: return WorkflowNodeExecutorResult.error(
                "Cannot find slot for environment = ${environment}, project = ${pipeline.slot.project.name}, qualifier = $actualQualifier",
                output = null,
            )
        // Is the build eligible for the target slot?
        if (!slotService.isBuildEligible(targetSlot, pipeline.build)) {
            return WorkflowNodeExecutorResult.error(
                "Build is not eligible for target slot",
                output = null,
            )
        }
        // Creating the pipeline
        val targetPipeline = slotService.startPipeline(targetSlot, pipeline.build)
        // OK
        return WorkflowNodeExecutorResult.success(
            SlotPipelineCreationWorkflowNodeExecutorOutput(
                targetPipelineId = targetPipeline.id,
            ).asJson()
        )
    }
}
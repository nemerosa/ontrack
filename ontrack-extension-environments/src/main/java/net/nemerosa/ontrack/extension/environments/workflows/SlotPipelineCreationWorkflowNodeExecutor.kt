package net.nemerosa.ontrack.extension.environments.workflows

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.environments.EnvironmentsExtensionFeature
import net.nemerosa.ontrack.extension.environments.Slot
import net.nemerosa.ontrack.extension.environments.service.EnvironmentService
import net.nemerosa.ontrack.extension.environments.service.SlotService
import net.nemerosa.ontrack.extension.environments.service.getPipelineById
import net.nemerosa.ontrack.extension.notifications.queue.NotificationQueueItem
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.extension.workflows.engine.WorkflowContext
import net.nemerosa.ontrack.extension.workflows.engine.WorkflowInstance
import net.nemerosa.ontrack.extension.workflows.execution.WorkflowNodeExecutor
import net.nemerosa.ontrack.extension.workflows.execution.WorkflowNodeExecutorResult
import net.nemerosa.ontrack.extension.workflows.notifications.WorkflowNotificationChannelNodeExecutor.Companion.CONTEXT_EVENT
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import net.nemerosa.ontrack.model.structure.StructureService
import org.springframework.stereotype.Component

@Component
class SlotPipelineCreationWorkflowNodeExecutor(
    extensionFeature: EnvironmentsExtensionFeature,
    private val slotService: SlotService,
    private val environmentService: EnvironmentService,
    private val securityService: SecurityService,
    private val structureService: StructureService,
) : AbstractExtension(extensionFeature), WorkflowNodeExecutor {

    override val id: String = "slot-pipeline-creation"
    override val displayName: String = "Pipeline creation"

    override suspend fun execute(
        workflowInstance: WorkflowInstance,
        workflowNodeId: String,
        workflowNodeExecutorResultFeedback: (output: JsonNode?) -> Unit
    ): WorkflowNodeExecutorResult {
        val (environment, configuredQualifier) = workflowInstance.workflow.getNode(workflowNodeId).data.parse<SlotPipelineCreationWorkflowNodeExecutorData>()

        return securityService.asAdmin {
            // Getting the target environment
            val targetEnvironment = environmentService.findByName(environment)
                ?: return@asAdmin WorkflowNodeExecutorResult.error(
                    "Environment name not found: ${environment}",
                    output = null
                )

            val (build, buildQualifier) = getBuildFromContext(workflowInstance.context)
            // Finding the target slot for the same project
            val qualifier = configuredQualifier ?: buildQualifier
            val targetSlot = slotService.findSlotsByEnvironment(targetEnvironment).find {
                it.project.id() == build.project.id() && it.qualifier == qualifier
            }
                ?: return@asAdmin WorkflowNodeExecutorResult.error(
                    "Cannot find slot for environment = ${environment}, project = ${build.project.name}, qualifier = $qualifier",
                    output = null,
                )
            // Is the build eligible for the target slot?
            if (!slotService.isBuildEligible(targetSlot, build)) {
                return@asAdmin WorkflowNodeExecutorResult.error(
                    "Build is not eligible for target slot",
                    output = null,
                )
            }
            // Creating the pipeline
            val targetPipeline = slotService.startPipeline(targetSlot, build)
            // OK
            WorkflowNodeExecutorResult.success(
                SlotPipelineCreationWorkflowNodeExecutorOutput(
                    targetPipelineId = targetPipeline.id,
                ).asJson()
            )
        }
    }

    private fun getBuildFromContext(context: WorkflowContext): QualifiedBuild =
        if (context.hasValue(SlotPipelineWorkflowContext.CONTEXT)) {
            getBuildFromSlotPipelineWorkflowContext(context)
        } else if (context.hasValue(CONTEXT_EVENT)) {
            getBuildFromEvent(context)
        } else {
            error("Cannot get build from workflow context")
        }

    private fun getBuildFromEvent(context: WorkflowContext): QualifiedBuild {
        val eventContext = context.parse<NotificationQueueItem>(CONTEXT_EVENT)
        val buildId = eventContext.entities[ProjectEntityType.BUILD]
            ?: error("Cannot find a reference to a build into the event context")
        val build = structureService.getBuild(ID.of(buildId))
        return QualifiedBuild(
            build = build,
            qualifier = Slot.DEFAULT_QUALIFIER,
        )
    }

    private fun getBuildFromSlotPipelineWorkflowContext(context: WorkflowContext): QualifiedBuild {
        val (pipelineId, _) = context
            .parse<SlotPipelineWorkflowContext>(SlotPipelineWorkflowContext.CONTEXT)

        // Getting the pipeline
        val pipeline = slotService.getPipelineById(pipelineId)

        return QualifiedBuild(
            build = pipeline.build,
            qualifier = pipeline.slot.qualifier,
        )
    }

    private data class QualifiedBuild(
        val build: Build,
        val qualifier: String,
    )
}
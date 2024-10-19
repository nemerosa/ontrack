package net.nemerosa.ontrack.extensions.environments.ui

import net.nemerosa.ontrack.extensions.environments.SlotPipeline
import net.nemerosa.ontrack.extensions.environments.SlotPipelineDeploymentFinishStatus
import net.nemerosa.ontrack.extensions.environments.SlotPipelineDeploymentStatus
import net.nemerosa.ontrack.extensions.environments.service.SlotService
import net.nemerosa.ontrack.graphql.schema.Mutation
import net.nemerosa.ontrack.graphql.support.TypedMutationProvider
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.structure.StructureService
import org.springframework.stereotype.Component

@Component
class SlotPipelineMutations(
    private val structureService: StructureService,
    private val slotService: SlotService,
) : TypedMutationProvider() {

    override val mutations: List<Mutation> = listOf(
        simpleMutation(
            name = "startSlotPipeline",
            description = "Starts a pipeline for a slot and a build",
            input = StartSlotPipelineInput::class,
            outputName = "pipeline",
            outputDescription = "Created pipeline",
            outputType = SlotPipeline::class,
        ) { input ->
            val slot = slotService.getSlotById(input.slotId)
            val build = structureService.getBuild(ID.of(input.buildId))
            slotService.startPipeline(
                slot = slot,
                build = build,
            )
        },
        simpleMutation(
            name = "startSlotPipelineDeployment",
            description = "Starts the deployment of a pipeline",
            input = StartSlotPipelineDeploymentInput::class,
            outputName = "deploymentStatus",
            outputDescription = "Pipeline deployment status",
            outputType = SlotPipelineDeploymentStatus::class,
        ) { input ->
            val pipeline = slotService.findPipelineById(input.pipelineId)
            pipeline?.let {
                slotService.startDeployment(
                    pipeline = it,
                    dryRun = false,
                )
            }
        },
        simpleMutation(
            name = "finishSlotPipelineDeployment",
            description = "Finishes the deployment of a pipeline",
            input = FinishSlotPipelineDeploymentInput::class,
            outputName = "finishStatus",
            outputDescription = "Status of the deployment",
            outputType = SlotPipelineDeploymentFinishStatus::class,
        ) { input ->
            val pipeline = slotService.findPipelineById(input.pipelineId)
            pipeline?.let {
                slotService.finishDeployment(
                    pipeline = it,
                    forcing = input.forcing,
                    message = input.message,
                )
            }
        },
    )
}

data class StartSlotPipelineInput(
    val slotId: String,
    val buildId: Int,
)

data class StartSlotPipelineDeploymentInput(
    val pipelineId: String,
)

data class FinishSlotPipelineDeploymentInput(
    val pipelineId: String,
    val forcing: Boolean = false,
    val message: String? = null,
)

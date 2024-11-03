package net.nemerosa.ontrack.extension.environments.workflows

import net.nemerosa.ontrack.extension.environments.service.SlotService
import net.nemerosa.ontrack.extension.workflows.registry.WorkflowParser
import net.nemerosa.ontrack.graphql.schema.Mutation
import net.nemerosa.ontrack.graphql.support.TypedMutationProvider
import org.springframework.stereotype.Component

@Component
class GQLSlotWorkflowMutations(
    val slotService: SlotService,
    val slotWorkflowService: SlotWorkflowService,
) : TypedMutationProvider() {
    override val mutations: List<Mutation> = listOf(
        simpleMutation(
            name = "addSlotWorkflow",
            description = "Add a workflow into an existing slot",
            input = AddSlotWorkflowInput::class,
            outputType = SlotWorkflow::class,
            outputName = "slotWorkflow",
            outputDescription = "Created slot workflow"
        ) { input ->
            val slot = slotService.getSlotById(input.slotId)
            val slotWorkflow = SlotWorkflow(
                slot = slot,
                trigger = input.trigger,
                workflow = WorkflowParser.parseYamlWorkflow(input.workflowYaml),
            )
            slotWorkflowService.addSlotWorkflow(slotWorkflow)
            slotWorkflow
        }
    )
}

data class AddSlotWorkflowInput(
    val slotId: String,
    val trigger: SlotWorkflowTrigger,
    val workflowYaml: String,
)
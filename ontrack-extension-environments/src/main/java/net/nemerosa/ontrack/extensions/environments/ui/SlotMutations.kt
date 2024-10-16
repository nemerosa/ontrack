package net.nemerosa.ontrack.extensions.environments.ui

import net.nemerosa.ontrack.extensions.environments.Slot
import net.nemerosa.ontrack.extensions.environments.service.EnvironmentService
import net.nemerosa.ontrack.extensions.environments.service.SlotService
import net.nemerosa.ontrack.graphql.schema.Mutation
import net.nemerosa.ontrack.graphql.support.ListRef
import net.nemerosa.ontrack.graphql.support.TypedMutationProvider
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.structure.StructureService
import org.springframework.stereotype.Component

@Component
class SlotMutations(
    private val structureService: StructureService,
    private val environmentService: EnvironmentService,
    private val slotService: SlotService,
) : TypedMutationProvider() {

    override val mutations: List<Mutation> = listOf(
        simpleMutation(
            name = "createSlots",
            description = "Creates several slots, one per provided environment",
            input = CreateSlotsInput::class,
            outputName = "slots",
            outputDescription = "List of the created slots",
            outputType = SlotList::class,
        ) { input ->
            val project = structureService.getProject(ID.of(input.projectId))
            val slots = input.environmentIds.map { environmentId ->
                val environment = environmentService.getById(environmentId)
                Slot(
                    environment = environment,
                    description = input.description,
                    project = project,
                    qualifier = input.qualifier ?: Slot.DEFAULT_QUALIFIER,
                ).apply {
                    slotService.addSlot(this)
                }
            }
            SlotList(slots = slots)
        }
    )
}

data class CreateSlotsInput(
    val projectId: Int,
    val qualifier: String?,
    val description: String?,
    @ListRef
    val environmentIds: List<String>,
)
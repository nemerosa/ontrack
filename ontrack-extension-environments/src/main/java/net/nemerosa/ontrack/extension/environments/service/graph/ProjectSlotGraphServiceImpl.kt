package net.nemerosa.ontrack.extension.environments.service.graph

import net.nemerosa.ontrack.extension.environments.Slot
import net.nemerosa.ontrack.extension.environments.service.SlotService
import net.nemerosa.ontrack.model.structure.Project
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ProjectSlotGraphServiceImpl(
    private val slotService: SlotService,
) : ProjectSlotGraphService {

    override fun slotGraph(project: Project, qualifier: String): SlotGraph {
        // Gets the list of all slots for this project and qualifier
        val slots = slotService.findSlotsByProject(project, qualifier)

        // List of nodes
        val slotNodes = slots.map {
            SlotNode(
                slot = it,
                parents = getParents(slots, it),
            )
        }.sortedBy { it.slot.environment.order }

        // Graph
        return SlotGraph(
            slotNodes = slotNodes,
        )
    }

    private fun getParents(slots: Set<Slot>, slot: Slot): List<Slot> {
        val lowerSlots = slots.filter { it.environment.order < slot.environment.order }
        return if (lowerSlots.isEmpty()) {
            emptyList()
        } else {
            val immediateLowerOrder = lowerSlots.maxOf { it.environment.order }
            lowerSlots.filter { it.environment.order == immediateLowerOrder }
        }
    }

}
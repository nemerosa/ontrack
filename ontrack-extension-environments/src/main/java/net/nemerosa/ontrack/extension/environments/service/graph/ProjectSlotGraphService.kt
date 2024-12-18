package net.nemerosa.ontrack.extension.environments.service.graph

import net.nemerosa.ontrack.extension.environments.Slot
import net.nemerosa.ontrack.model.structure.Project

interface ProjectSlotGraphService {

    fun slotGraph(project: Project, qualifier: String = Slot.DEFAULT_QUALIFIER): SlotGraph

}
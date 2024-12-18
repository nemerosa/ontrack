package net.nemerosa.ontrack.extension.environments.service.graph

import net.nemerosa.ontrack.extension.environments.Slot

data class SlotNode(
    val slot: Slot,
    val parents: List<Slot>,
)

package net.nemerosa.ontrack.extension.environments.workflows

import net.nemerosa.ontrack.extension.environments.events.EnvironmentsEvents
import net.nemerosa.ontrack.model.annotations.APIDescription

data class SlotTemplatingFunctionParameters(
    @APIDescription("ID of the slot. Defaults to ${EnvironmentsEvents.EVENT_SLOT_ID}")
    val id: String? = null,
)

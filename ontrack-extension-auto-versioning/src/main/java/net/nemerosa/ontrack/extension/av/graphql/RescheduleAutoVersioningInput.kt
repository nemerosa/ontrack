package net.nemerosa.ontrack.extension.av.graphql

import net.nemerosa.ontrack.common.api.APIDescription

data class RescheduleAutoVersioningInput(
    @APIDescription("UUID of the order to reschedule")
    val uuid: String,
)

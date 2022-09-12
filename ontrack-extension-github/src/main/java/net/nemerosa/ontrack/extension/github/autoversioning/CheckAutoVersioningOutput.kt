package net.nemerosa.ontrack.extension.github.autoversioning

import net.nemerosa.ontrack.model.annotations.APIDescription

data class CheckAutoVersioningOutput(
    @APIDescription("UUID of the payload being processed in the background")
    val uuid: String,
)
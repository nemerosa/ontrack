package net.nemerosa.ontrack.model.trigger

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.model.annotations.APIDescription

/**
 * Associate the ID of a [Trigger] to some data
 */
data class TriggerData(
    @APIDescription("ID of the trigger")
    val id: String,
    @APIDescription("Data associated with this trigger")
    val data: JsonNode,
)

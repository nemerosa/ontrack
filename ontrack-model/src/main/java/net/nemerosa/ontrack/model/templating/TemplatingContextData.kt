package net.nemerosa.ontrack.model.templating

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.model.annotations.APIDescription

/**
 * Data to be rendered
 *
 * @property id ID of the [TemplatingContextHandler]
 * @property data Serialized [TemplatingContext] for the handler
 */
data class TemplatingContextData(
    @APIDescription("ID of the templating context handler in charge to rendering this data")
    val id: String,
    @APIDescription("Data associated with the context, used by the handler associated with the ID")
    val data: JsonNode,
)

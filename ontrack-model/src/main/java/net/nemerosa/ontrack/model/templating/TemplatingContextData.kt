package net.nemerosa.ontrack.model.templating

import com.fasterxml.jackson.databind.JsonNode

/**
 * Data to be rendered
 *
 * @property id ID of the [TemplatingContextHandler]
 * @property data Serialized [TemplatingContext] for the handler
 */
data class TemplatingContextData(
    val id: String,
    val data: JsonNode,
)

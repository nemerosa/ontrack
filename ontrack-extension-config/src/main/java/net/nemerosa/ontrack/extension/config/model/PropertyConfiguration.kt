package net.nemerosa.ontrack.extension.config.model

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.model.annotations.APIDescription

@APIDescription("Property configuration")
data class PropertyConfiguration(
    @APIDescription("FQCN of the property type")
    val type: String,
    @APIDescription("Configuration data for the property")
    val data: JsonNode,
)

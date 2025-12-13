package net.nemerosa.ontrack.extension.config.model

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.model.annotations.APIDescription

@APIDescription("Extension configuration")
data class ExtensionConfiguration(
    @APIDescription("ID of the extension")
    val id: String,
    @APIDescription("Configuration data for the extension")
    val data: JsonNode,
)

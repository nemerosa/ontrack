package net.nemerosa.ontrack.extension.config.ci.model

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.model.annotations.APIDescription

@APIDescription("Condition configuration")
data class CIConditionConfig(
    @APIDescription("Identifier of the condition")
    val name: String,
    @APIDescription("Configuration for the condition")
    val config: JsonNode,
)

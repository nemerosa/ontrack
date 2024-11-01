package net.nemerosa.ontrack.extension.environments

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.model.annotations.APIDescription

data class SlotPipelineDataInputValue(
    @APIDescription("ID of the configured admission rule")
    val configId: String,
    @APIDescription("Input data for the rule")
    val data: JsonNode,
)
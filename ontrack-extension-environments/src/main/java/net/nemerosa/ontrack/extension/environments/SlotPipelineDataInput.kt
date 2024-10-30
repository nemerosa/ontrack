package net.nemerosa.ontrack.extension.environments

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.graphql.support.ListRef
import net.nemerosa.ontrack.model.annotations.APIDescription

data class SlotPipelineDataInput(
    @APIDescription("Name of the rule")
    val name: String,
    @APIDescription("List of values for the rule")
    @ListRef(embedded = true)
    val values: List<SlotPipelineDataInputValue>,
)

data class SlotPipelineDataInputValue(
    @APIDescription("Name of the field")
    val name: String,
    @APIDescription("Value for the field")
    val value: JsonNode?,
)


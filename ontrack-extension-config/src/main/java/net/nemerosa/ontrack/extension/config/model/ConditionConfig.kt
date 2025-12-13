package net.nemerosa.ontrack.extension.config.model

import com.fasterxml.jackson.databind.JsonNode

data class ConditionConfig(
    val name: String,
    val config: JsonNode,
)

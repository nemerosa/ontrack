package net.nemerosa.ontrack.extension.config.ci.model

import com.fasterxml.jackson.databind.JsonNode

data class CIProjectConfig(
    override val properties: Map<String, JsonNode> = emptyMap(),
) : CIPropertiesConfig

package net.nemerosa.ontrack.extension.config.ci.model

import com.fasterxml.jackson.databind.JsonNode

data class CIBuildConfig(
    override val properties: Map<String, JsonNode> = emptyMap(),
    val autoVersioningCheck: Boolean? = null,
) : CIPropertiesConfig

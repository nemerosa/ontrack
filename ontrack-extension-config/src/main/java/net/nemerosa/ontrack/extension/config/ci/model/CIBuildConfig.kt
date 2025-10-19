package net.nemerosa.ontrack.extension.config.ci.model

import com.fasterxml.jackson.annotation.JsonAnySetter
import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.model.annotations.APIDescription

data class CIBuildConfig(
    @APIDescription("Name of the build as a template.")
    val buildName: String? = null,
    override val properties: Map<String, JsonNode> = emptyMap(),
    @field:JsonAnySetter
    override val extensions: Map<String, JsonNode> = mutableMapOf()
) : CIPropertiesConfig, CIExtensionsConfig

package net.nemerosa.ontrack.extension.config.ci.model

import com.fasterxml.jackson.annotation.JsonAnySetter
import com.fasterxml.jackson.databind.JsonNode

data class CIBranchConfig(
    override val properties: Map<String, JsonNode> = emptyMap(),
    val validations: Map<String, JsonNode> = emptyMap(),
    val promotions: Map<String, CIPromotionConfig> = emptyMap(),
    @field:JsonAnySetter
    override val extensions: Map<String, JsonNode> = mutableMapOf()
) : CIPropertiesConfig, CIExtensionsConfig

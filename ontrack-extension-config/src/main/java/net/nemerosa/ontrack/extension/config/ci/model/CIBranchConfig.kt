package net.nemerosa.ontrack.extension.config.ci.model

import com.fasterxml.jackson.annotation.JsonAnySetter
import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.av.config.AutoVersioningConfig

data class CIBranchConfig(
    override val properties: Map<String, JsonNode> = emptyMap(),
    val validations: Map<String, JsonNode> = emptyMap(),
    val promotions: Map<String, CIPromotionConfig> = emptyMap(),
    val autoVersioning: AutoVersioningConfig? = null,
    @field:JsonAnySetter
    val extensions: Map<String, JsonNode> = mutableMapOf()
) : CIPropertiesConfig

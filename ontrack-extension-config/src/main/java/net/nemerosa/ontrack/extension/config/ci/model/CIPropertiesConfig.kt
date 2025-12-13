package net.nemerosa.ontrack.extension.config.ci.model

import com.fasterxml.jackson.databind.JsonNode

interface CIPropertiesConfig {
    val properties: Map<String, JsonNode>
}
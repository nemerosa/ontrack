package net.nemerosa.ontrack.extension.config.ci.model

import com.fasterxml.jackson.databind.JsonNode

interface CIExtensionsConfig {
    val extensions: Map<String, JsonNode>
}
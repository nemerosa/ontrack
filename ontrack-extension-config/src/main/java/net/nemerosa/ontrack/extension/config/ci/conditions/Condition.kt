package net.nemerosa.ontrack.extension.config.ci.conditions

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.config.ci.engine.CIEngine

interface Condition {
    val name: String
    fun matches(ciEngine: CIEngine, config: JsonNode, env: Map<String, String>): Boolean
}

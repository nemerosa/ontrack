package net.nemerosa.ontrack.extension.config.ci.properties

import com.fasterxml.jackson.databind.JsonNode

interface PropertyAlias {
    val alias: String
    val type: String
    fun parseConfig(data: JsonNode): JsonNode
}
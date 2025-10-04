package net.nemerosa.ontrack.extension.config.ci.validations

import com.fasterxml.jackson.databind.JsonNode

interface ValidationDataTypeAlias {
    val alias: String
    val type: String
    fun parseConfig(data: JsonNode): JsonNode
}
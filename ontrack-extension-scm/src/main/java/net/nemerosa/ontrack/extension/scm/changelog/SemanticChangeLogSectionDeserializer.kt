package net.nemerosa.ontrack.extension.scm.changelog

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.TextNode

class SemanticChangeLogSectionDeserializer : JsonDeserializer<SemanticChangeLogSection>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): SemanticChangeLogSection {
        val node: JsonNode = p.readValueAsTree()
        return if (node is TextNode) {
            val text = node.textValue()
            if (text.contains("=")) {
                val type = text.substringBefore("=")
                val title = text.substringAfter("=")
                SemanticChangeLogSection(type, title)
            } else {
                SemanticChangeLogSection(text, text)
            }
        } else if (node is ObjectNode) {
            val type = node.get("type")?.textValue() ?: ""
            val title = node.get("title")?.textValue() ?: ""
            SemanticChangeLogSection(type, title)
        } else {
            throw IllegalArgumentException("Unsupported JSON node type for SemanticChangeLogSection: $node")
        }
    }
}

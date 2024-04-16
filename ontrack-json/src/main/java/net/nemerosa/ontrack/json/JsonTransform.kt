package net.nemerosa.ontrack.json

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.TextNode

fun JsonNode.transform(textTransform: (text: String) -> String): JsonNode =
    when {

        isTextual -> TextNode(textTransform(asText()))

        isArray -> map {
            it.transform(textTransform)
        }.asJson()

        isObject -> fields().asSequence().map { (name, value) ->
            name to value.transform(textTransform)
        }.toMap().asJson()

        else -> this
    }

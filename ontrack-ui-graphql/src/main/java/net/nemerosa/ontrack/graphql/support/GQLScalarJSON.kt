package net.nemerosa.ontrack.graphql.support

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.*
import graphql.language.StringValue
import graphql.schema.Coercing
import graphql.schema.GraphQLScalarType
import net.nemerosa.ontrack.json.JsonParseException
import net.nemerosa.ontrack.json.ObjectMapperFactory
import java.io.IOException

/**
 * JSON scalar type.
 */
class GQLScalarJSON private constructor() : GraphQLScalarType(
        "JSON",
        "Custom JSON value",
        object : Coercing<Any, JsonNode> {

            private val mapper = ObjectMapperFactory.create()

            override fun serialize(input: Any): JsonNode {
                val json: JsonNode = when (input) {
                    is JsonNode -> input
                    is String -> try {
                        mapper.readTree(input)
                    } catch (e: IOException) {
                        throw JsonParseException(e)
                    }
                    else -> mapper.valueToTree<JsonNode>(input)
                }
                return json.obfuscate()
            }

            override fun parseValue(input: Any): JsonNode {
                return serialize(input)
            }

            override fun parseLiteral(input: Any): JsonNode? {
                return if (input is StringValue) {
                    serialize(input)
                } else {
                    null
                }
            }

            private fun JsonNode.obfuscate(): JsonNode {
                val factory: JsonNodeFactory = mapper.nodeFactory
                return when (this) {
                // Transforms each element
                    is ArrayNode ->
                        ArrayNode(
                                factory,
                                map { it.obfuscate() }
                        )
                // Filters at field level
                    is ObjectNode ->
                        ObjectNode(
                                factory,
                                fields().asSequence()
                                        .associate { (name: String, value: JsonNode?) ->
                                            when (value) {
                                                is TextNode ->
                                                    when {
                                                        name.toLowerCase().contains("password") -> name to NullNode.instance
                                                        name.toLowerCase().contains("token") -> name to NullNode.instance
                                                        else -> name to value
                                                    }
                                                else ->
                                                    name to value.obfuscate()
                                            }
                                        }
                        )
                // No transformation for an end element
                    else -> this
                }
            }
        }
) {
    companion object {
        @JvmField
        val INSTANCE: GraphQLScalarType = GQLScalarJSON()
    }
}

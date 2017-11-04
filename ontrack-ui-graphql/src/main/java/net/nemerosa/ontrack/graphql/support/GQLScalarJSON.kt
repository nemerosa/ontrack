package net.nemerosa.ontrack.graphql.support

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.*
import graphql.language.NullValue
import graphql.language.StringValue
import graphql.schema.Coercing
import graphql.schema.CoercingParseValueException
import graphql.schema.GraphQLScalarType
import net.nemerosa.ontrack.json.ObjectMapperFactory

/**
 * JSON scalar type.
 */
class GQLScalarJSON private constructor() : GraphQLScalarType(
        "JSON",
        "Custom JSON value",
        object : Coercing<JsonNode, JsonNode> {

            private val mapper = ObjectMapperFactory.create()

            override fun serialize(dataFetcherResult: Any): JsonNode {
                val json = when (dataFetcherResult) {
                    is JsonNode -> dataFetcherResult
                    else -> mapper.valueToTree<JsonNode>(dataFetcherResult)
                }
                return json.obfuscate()
            }

            override fun parseValue(input: Any): JsonNode =
                    when (input) {
                        is String -> mapper.readTree(input)
                        is JsonNode -> input
                        else -> throw CoercingParseValueException("Cannot parse value for ${input::class}")
                    }

            override fun parseLiteral(input: Any): JsonNode? =
                    when (input) {
                        is NullValue -> NullNode.instance
                        is StringValue -> mapper.readTree(input.value)
                        else -> null // Meaning invalid
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

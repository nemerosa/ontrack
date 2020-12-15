package net.nemerosa.ontrack.graphql.support

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.*
import graphql.language.*
import graphql.schema.Coercing
import graphql.schema.CoercingParseValueException
import graphql.schema.GraphQLScalarType
import net.nemerosa.ontrack.json.ObjectMapperFactory
import net.nemerosa.ontrack.json.asJson


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
                is FloatValue -> DoubleNode.valueOf(input.value.toDouble())
                is StringValue -> TextNode.valueOf(input.value)
                is IntValue -> IntNode.valueOf(input.value.toInt())
                is BooleanValue -> BooleanNode.valueOf(input.isValue)
                // TODO ArrayValue
                is ObjectValue -> {
                    val `object` = ObjectNode(mapper.nodeFactory)
                    input.objectFields.forEach { of ->
                        `object`.set<JsonNode>(of.name, parseLiteral(of.value))
                    }
                    `object`
                }
                else -> throw CoercingParseValueException("Cannot parse value for ${input::class}")
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

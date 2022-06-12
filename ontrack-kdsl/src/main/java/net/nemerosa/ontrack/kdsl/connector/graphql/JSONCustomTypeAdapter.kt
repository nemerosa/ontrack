package net.nemerosa.ontrack.kdsl.connector.graphql

import com.apollographql.apollo.api.CustomTypeAdapter
import com.apollographql.apollo.api.CustomTypeValue
import com.apollographql.apollo.api.ScalarType
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.*

class JSONCustomTypeAdapter : CustomTypeAdapter<JsonNode> {

    private val jsonNodeFactory = JsonNodeFactory(false)

    override fun decode(value: CustomTypeValue<*>): JsonNode =
        when (value) {
            is CustomTypeValue.GraphQLNull -> NullNode.instance
            is CustomTypeValue.GraphQLBoolean -> BooleanNode.valueOf(value.value)
            is CustomTypeValue.GraphQLNumber -> {
                val number = value.value
                when (number) {
                    is Int -> IntNode.valueOf(number)
                    is Long -> LongNode.valueOf(number)
                    is Float -> FloatNode.valueOf(number)
                    is Double -> DoubleNode.valueOf(number)
                    else -> error("Unsupported GraphQL number type: ${number::class}")
                }
            }
            is CustomTypeValue.GraphQLString -> TextNode.valueOf(value.value)
            is CustomTypeValue.GraphQLJsonList -> ArrayNode(jsonNodeFactory).apply {
                value.value.forEach { element ->
                    if (element is CustomTypeValue<*>) {
                        add(decode(element))
                    } else {
                        error("Unsupported property in GraphQL object: ${element::class}")
                    }
                }
            }
            is CustomTypeValue.GraphQLJsonObject -> TODO()
            else -> error("Unsupported GraphQL type: ${value::class}")
        }

    override fun encode(value: JsonNode): CustomTypeValue<*> =
        when {
            value.isNull -> CustomTypeValue.GraphQLNull
            value.isBoolean -> CustomTypeValue.GraphQLBoolean(value.asBoolean())
            value.isInt -> CustomTypeValue.GraphQLNumber(value.asInt())
            value.isLong -> CustomTypeValue.GraphQLNumber(value.asLong())
            value.isFloat -> CustomTypeValue.GraphQLNumber(value.asDouble())
            value.isDouble -> CustomTypeValue.GraphQLNumber(value.asDouble())
            value.isTextual -> CustomTypeValue.GraphQLString(value.asText())
            value.isArray -> CustomTypeValue.GraphQLJsonList(
                value.map {
                    encode(it)
                }
            )
            value.isObject -> CustomTypeValue.GraphQLJsonObject(
                value.fields().asSequence().associate { (name, node) ->
                    name to encode(node)
                }
            )
            else -> error("Unsupported type of Json value: ${value.nodeType}")
        }

    companion object {
        val TYPE = object : ScalarType {
            override fun typeName(): String = "JSON"
            override fun className(): String = JsonNode::class.java.name
        }
    }

}
package net.nemerosa.ontrack.kdsl.connector.graphql

import com.apollographql.apollo.api.CustomTypeAdapter
import com.apollographql.apollo.api.CustomTypeValue
import com.apollographql.apollo.api.ScalarType
import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.json.asJson

class JSONCustomTypeAdapter : CustomTypeAdapter<JsonNode> {

    override fun decode(value: CustomTypeValue<*>): JsonNode =
        value.value.asJson()

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
                value.mapNotNull {
                    encodePrimitive(it)
                }
            )
            value.isObject -> {
                val map = mutableMapOf<String, Any>()
                value.fields().forEach { (name, node) ->
                    val encoded = encodePrimitive(node)
                    if (encoded != null)
                        map[name] = encoded
                }
                CustomTypeValue.GraphQLJsonObject(map)
            }
            else -> error("Unsupported type of Json value: ${value.nodeType}")
        }

    private fun encodePrimitive(value: JsonNode): Any? =
        when {
            value.isNull -> null
            value.isBoolean -> value.asBoolean()
            value.isInt -> value.asInt()
            value.isLong -> value.asLong()
            value.isFloat -> value.asDouble()
            value.isDouble -> value.asDouble()
            value.isTextual -> value.asText()
            value.isArray -> value.map {
                encodePrimitive(it)
            }
            value.isObject -> value.fields()
                .asSequence()
                .associate { (name, node) ->
                    name to encodePrimitive(node)
                }
            else -> error("Unsupported type of Json value: ${value.nodeType}")
        }

    companion object {
        val TYPE = object : ScalarType {
            override fun typeName(): String = "JSON"
            override fun className(): String = JsonNode::class.java.name
        }
    }

}
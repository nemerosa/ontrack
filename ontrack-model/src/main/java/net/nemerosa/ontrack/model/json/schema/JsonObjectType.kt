package net.nemerosa.ontrack.model.json.schema

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.annotation.JsonSerialize

class JsonObjectType(
    title: String,
    description: String?,
    val properties: Map<String, JsonType>,
    val required: List<String>,
    val additionalProperties: Boolean = false,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val minProperties: Int? = null,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val maxProperties: Int? = null,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val oneOf: JsonOneOf? = null,
) : AbstractJsonNamedType(type = "object", title = title, description = description)

@JsonSerialize(using = JsonOneOfSerializer::class)
class JsonOneOf(
    val conditions: List<JsonCondition>,
)

@JsonSerialize(using = JsonConditionSerializer::class)
class JsonCondition(
    internal val constProperty: JsonConstProperty,
    internal val refProperty: JsonRefProperty,
)

class JsonConstProperty(
    val name: String,
    val value: String,
)

class JsonRefProperty(
    val name: String,
    val ref: String,
)

class JsonOneOfSerializer : JsonSerializer<JsonOneOf>() {
    override fun serialize(value: JsonOneOf, gen: JsonGenerator, serializers: SerializerProvider) {
        gen.writeStartArray()
        value.conditions.forEach {
            gen.writeStartObject()
            gen.writeObjectField("properties", it)
            gen.writeEndObject()
        }
        gen.writeEndArray()
    }
}

class JsonConditionSerializer : JsonSerializer<JsonCondition>() {
    override fun serialize(condition: JsonCondition, gen: JsonGenerator, serializers: SerializerProvider) {
        gen.writeStartObject()
        gen.writeObjectField(
            condition.constProperty.name, mapOf(
                "const" to condition.constProperty.value,
            )
        )
        gen.writeObjectField(
            condition.refProperty.name, mapOf(
                "\$ref" to "#/\$defs/${condition.refProperty.ref}"
            )
        )
        gen.writeEndObject()
    }
}
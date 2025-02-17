package net.nemerosa.ontrack.model.json.schema

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.common.hasDefaultValue
import net.nemerosa.ontrack.model.annotations.getPropertyDescription
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.starProjectedType

fun jsonSchema(
    ref: String,
    id: String,
    title: String,
    description: String,
    root: KClass<*>,
    clsGetter: (cls: KClass<out DynamicJsonSchemaProvider>) -> DynamicJsonSchemaProvider,
): JsonSchema {

    val defs = mutableMapOf<String, JsonType>()
    val builder = JsonSchemaBuilder(
        clsGetter = clsGetter,
        defs = defs,
    )
    val rootType = builder.toRoot(root, description)

    return JsonSchema(
        ref = ref,
        id = id,
        title = title,
        description = description,
        defs = defs,
        root = rootType,
    )
}

interface JsonTypeBuilder {
    fun toType(type: KType, description: String? = null): JsonType
}

private class JsonSchemaBuilder(
    private val clsGetter: (cls: KClass<out DynamicJsonSchemaProvider>) -> DynamicJsonSchemaProvider,
    private val defs: MutableMap<String, JsonType>,
) : JsonTypeBuilder {

    fun toRoot(type: KClass<*>, description: String? = null): JsonObjectType =
        toObject(type.starProjectedType, description)

    override fun toType(type: KType, description: String?): JsonType =
        when {
            type.classifier == String::class -> JsonStringType(description)
            type.classifier == Int::class -> JsonIntType(description)
            type.classifier == Long::class -> JsonLongType(description)
            type.classifier == Boolean::class -> JsonBooleanType(description)
            type.classifier == List::class -> toList(type, description)
            type.classifier == JsonNode::class -> JsonRawJsonType(description)
            type.toString().startsWith("net.nemerosa.ontrack.") -> toObject(type, description)
            else -> error("$type is not supported")
        }

    private fun toList(type: KType, description: String? = null): JsonArrayType {
        val itemType = type.arguments.firstOrNull()?.type
            ?.run { toType(this, description) }
            ?: error("List must be typed")
        return JsonArrayType(itemType, description)
    }

    private fun toObject(type: KType, description: String? = null): JsonObjectType {
        val cls = type.classifier as KClass<*>

        val excludedProperties = mutableSetOf<String>()
        val dynamicJsonSchema = cls.findAnnotation<DynamicJsonSchema>()
        if (dynamicJsonSchema != null) {
            excludedProperties += dynamicJsonSchema.discriminatorProperty
            excludedProperties += dynamicJsonSchema.configurationProperty
        }

        val oProperties = mutableMapOf<String, JsonType>()
        val oRequired = mutableListOf<String>()
        val properties = cls.memberProperties
        for (property in properties) {
            if (property.name !in excludedProperties) {
                val propertyReturnType = property.returnType
                val schemaRef = property.findAnnotation<JsonSchemaRef>()
                if (schemaRef != null) {
                    oProperties[property.name] = JsonRefType(
                        ref = schemaRef.value,
                        description = getPropertyDescription(property),
                    )
                } else {
                    val propertyType = toType(propertyReturnType, getPropertyDescription(property))
                    oProperties[property.name] = propertyType
                }
                if (!property.returnType.isMarkedNullable && !property.hasDefaultValue(cls)) {
                    oRequired += property.name
                }
            }
        }

        val oneOf = dynamicJsonSchema?.let {
            setupDynamicJsonSchema(
                cls = cls,
                dynamicJsonSchema = it,
                oProperties = oProperties,
                oRequired = oRequired
            )
        }

        return JsonObjectType(
            title = cls.java.simpleName,
            description = description,
            properties = oProperties,
            required = oRequired,
            additionalProperties = false,
            oneOf = oneOf,
        )
    }

    private fun setupDynamicJsonSchema(
        cls: KClass<*>,
        dynamicJsonSchema: DynamicJsonSchema,
        oProperties: MutableMap<String, JsonType>,
        oRequired: MutableList<String>
    ): JsonOneOf {
        // Getting the provider
        val provider = clsGetter(dynamicJsonSchema.provider)

        // Gets the corresponding properties in the holding class
        val clsProperties = cls.memberProperties.associateBy { it.name }
        val discriminatorProperty = clsProperties[dynamicJsonSchema.discriminatorProperty]
            ?: error("Cannot find property ${dynamicJsonSchema.discriminatorProperty} in $cls")
        clsProperties[dynamicJsonSchema.configurationProperty]
            ?: error("Cannot find property ${dynamicJsonSchema.configurationProperty} in $cls")

        // Both properties are required
        if (dynamicJsonSchema.required) {
            oRequired += dynamicJsonSchema.configurationProperty
            oRequired += dynamicJsonSchema.discriminatorProperty
        }

        // Enum property
        val discriminatorValues = provider.discriminatorValues.sorted()
        oProperties[dynamicJsonSchema.discriminatorProperty] = JsonEnumType(
            values = discriminatorValues,
            description = getPropertyDescription(discriminatorProperty),
        )

        // All definitions
        defs += provider.getConfigurationTypes(this).mapKeys { (id, _) ->
            provider.toRef(id)
        }

        // Conditions (oneOf) on the object type
        return JsonOneOf(
            conditions = discriminatorValues.map { id ->
                JsonCondition(
                    constProperty = JsonConstProperty(
                        name = dynamicJsonSchema.discriminatorProperty,
                        value = id,
                    ),
                    refProperty = JsonRefProperty(
                        name = dynamicJsonSchema.configurationProperty,
                        ref = provider.toRef(id)
                    )
                )
            }
        )
    }

}

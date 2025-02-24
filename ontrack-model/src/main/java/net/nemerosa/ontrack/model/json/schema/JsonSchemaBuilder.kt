package net.nemerosa.ontrack.model.json.schema

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.common.hasDefaultValue
import net.nemerosa.ontrack.model.annotations.APIIgnore
import net.nemerosa.ontrack.model.annotations.APIOptional
import net.nemerosa.ontrack.model.annotations.getPropertyDescription
import net.nemerosa.ontrack.model.annotations.getPropertyName
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component
import java.time.Duration
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.*
import kotlin.reflect.jvm.jvmName

@Component
class JsonSchemaBuilderService(
    private val applicationContext: ApplicationContext,
) : JsonTypeBuilder {

    fun createSchema(
        ref: String,
        id: String,
        title: String,
        description: String,
        root: KClass<*>
    ): JsonSchema {
        val defs = mutableMapOf<String, JsonType>()
        val builder = createSchemaBuilder(defs)
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

    fun createSchema(
        ref: String,
        id: String,
        title: String,
        description: String,
        root: JsonTypeProvider,
    ): JsonSchema {
        val defs = mutableMapOf<String, JsonType>()
        val builder = createSchemaBuilder(defs)
        val rootType = root.jsonType(builder)
        return if (rootType is JsonObjectType) {
            JsonSchema(
                ref = ref,
                id = id,
                title = title,
                description = description,
                defs = defs,
                root = rootType,
            )
        } else {
            error("Root type must be an object")
        }
    }

    override fun toType(type: KType, description: String?): JsonType {
        val defs = mutableMapOf<String, JsonType>()
        val builder = createSchemaBuilder(defs)
        return builder.toType(type, description)
    }

    private fun createSchemaBuilder(defs: MutableMap<String, JsonType>) =
        JsonSchemaBuilder(
            clsGetter = { cls ->
                applicationContext.getBeansOfType(cls.java).values.single()
            },
            defs = defs,
        )
}

private class JsonSchemaBuilder(
    private val clsGetter: (cls: KClass<out DynamicJsonSchemaProvider>) -> DynamicJsonSchemaProvider,
    private val defs: MutableMap<String, JsonType>,
) : JsonTypeBuilder {

    fun toRoot(type: KClass<*>, description: String? = null): JsonObjectType =
        toObject(type.starProjectedType, description)

    override fun toType(type: KType, description: String?): JsonType {
        val cls = type.classifier as KClass<*>
        return when {
            cls == String::class -> JsonStringType(description)
            cls == Int::class -> JsonIntType(description)
            cls == Long::class -> JsonLongType(description)
            cls == Boolean::class -> JsonBooleanType(description)
            cls == List::class -> toList(type, description)
            cls == JsonNode::class -> JsonRawJsonType(description)
            cls == Duration::class -> JsonDurationType(description)
            cls.isSubclassOf(Enum::class) -> toEnumType(cls, description)
            cls.jvmName.startsWith("net.nemerosa.ontrack.") -> toObject(type, description)
            else -> error("$type is not supported")
        }
    }

    private fun toEnumType(cls: KClass<*>, description: String?) = JsonEnumType(
        values = cls.java.enumConstants.map { it.toString() },
        description = description,
    )

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
            if (property.name !in excludedProperties && !property.hasAnnotation<APIIgnore>()) {
                val propertyName = getPropertyName(property)
                val propertyReturnType = property.returnType
                val schemaRef = property.findAnnotation<JsonSchemaRef>()
                if (schemaRef != null) {
                    oProperties[propertyName] = JsonRefType(
                        ref = schemaRef.value,
                        description = getPropertyDescription(property),
                    )
                } else {
                    val propertyType = toType(propertyReturnType, getPropertyDescription(property))
                    oProperties[propertyName] = propertyType
                }
                if (!property.returnType.isMarkedNullable && !property.hasDefaultValue(cls) && !property.hasAnnotation<APIOptional>()) {
                    oRequired += propertyName
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

        // Placeholder for the configuration
        oProperties[dynamicJsonSchema.configurationProperty] = JsonEmptyType.INSTANCE

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

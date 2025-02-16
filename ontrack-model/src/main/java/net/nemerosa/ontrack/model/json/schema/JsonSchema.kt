package net.nemerosa.ontrack.model.json.schema

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import net.nemerosa.ontrack.common.hasDefaultValue
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.model.annotations.getPropertyDescription
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.KType
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.starProjectedType

fun jsonSchema(
    id: String,
    title: String,
    description: String,
    root: KClass<*>,
    configuration: JsonSchemaConfigurator.() -> Unit = {},
): JsonNode {

    val configurator = JsonSchemaConfigurator()
    configurator.configuration()
    val schemaConfiguration = configurator.toJsonSchemaConfiguration()

    val headers = mapOf(
        "${'$'}schema" to "https://json-schema.org/draft/2020-12/schema",
        "${'$'}id" to id,
        "title" to title,
        "description" to description,
    )

    val rootType = JsonSchemaBuilder(
        configuration = schemaConfiguration,
    ).toRoot(root)

    return (headers + rootType).asJson()
}

private class JsonSchemaBuilder(
    private val configuration: JsonSchemaConfiguration,
) {

    fun toRoot(type: KClass<*>, description: String? = null): Map<String, Any?> {
        val desc = mutableMapOf<String, Any?>()

        if (configuration.defs.isNotEmpty()) {
            val allDefs = mutableMapOf<String, Any?>()
            configuration.defs.forEach { def ->
                val defTypes = def(::toType)
                allDefs += defTypes
            }
            desc["${'$'}defs"] = allDefs
        }

        desc += toType(type.starProjectedType, description)
        return desc.toMap()
    }

    private fun toType(type: KType, description: String? = null): Map<String, Any?> =
        when {
            type.classifier == String::class -> toString(description)
            type.classifier == Long::class -> toLong(description)
            type.classifier == Boolean::class -> toBoolean(description)
            type.classifier == List::class -> toList(type, description)
            type.classifier == JsonNode::class -> toJson(description)
            type.toString().startsWith("net.nemerosa.ontrack.") -> toObject(type, description)
            else -> error("$type is not supported")
        }

    private fun toString(description: String? = null): Map<String, Any?> =
        mapOf(
            "title" to "String",
            "type" to "string",
            "description" to description,
        )

    private fun toLong(description: String? = null): Map<String, Any?> =
        mapOf(
            "title" to "Long",
            "type" to "integer",
            "description" to description,
        )

    private fun toBoolean(description: String? = null): Map<String, Any?> =
        mapOf(
            "title" to "Boolean",
            "type" to "boolean",
            "description" to description,
        )

    private fun toJson(description: String? = null): Map<String, Any?> =
        mapOf(
            "title" to "JSON",
            "type" to emptyMap<String, String>(),
            "description" to description,
        )

    private fun toList(type: KType, description: String? = null): Map<String, Any?> {
        val itemType = type.arguments.firstOrNull()?.type
            ?: error("List must be typed")
        return mapOf(
            "type" to "array",
            "description" to description,
            "items" to toType(itemType),
        )
    }

    private fun toObject(type: KType, description: String? = null): Map<String, Any?> {
        val cls = type.classifier as KClass<*>
        val properties = cls.memberProperties
        val o = mutableMapOf(
            "title" to cls.simpleName,
            "description" to description,
            "type" to "object",
            "properties" to properties
                .filter { property ->
                    configuration.oneOfs[property] == null
                }
                .associate { property ->
                    val propertyTypeBuilder = configuration.propertyTypes[property]
                    if (propertyTypeBuilder != null) {
                        val propertyType = propertyTypeBuilder(property)
                        if (propertyType is ObjectNode) {
                            propertyType.put("description", getPropertyDescription(property))
                        }
                        property.name to propertyType
                    } else {
                        val propertyType = property.returnType
                        property.name to toType(propertyType, getPropertyDescription(property))
                    }
                },
            "required" to properties
                .filter { !it.returnType.isMarkedNullable && !it.hasDefaultValue(cls) }
                .map { it.name },
            "additionalProperties" to false,
        )

        val oneOfBuilder = configuration.oneOfs.entries.singleOrNull()
        if (oneOfBuilder != null) {
            val (property, builder) = oneOfBuilder
            if (property in properties) {
                o["oneOf"] = builder(property, ::toType)
            }
        }

        return o.toMap()
    }

}

class JsonSchemaConfigurator {

    private val defs = mutableListOf<DefBuilder>()
    private val propertyTypes = mutableMapOf<KProperty1<*, *>, (property: KProperty1<*, *>) -> JsonNode>()
    private val oneOfs = mutableMapOf<KProperty1<*, *>, OneOfBuilder>()

    fun defs(code: DefBuilder) {
        defs += code
    }

    fun propertyType(
        property: KProperty1<*, *>,
        typeBuilder: (property: KProperty1<*, *>) -> JsonNode
    ) {
        propertyTypes[property] = typeBuilder
    }

    fun oneOf(
        property: KProperty1<*, *>,
        conditionsBuilder: (
            property: KProperty1<*, *>,
            toSchema: ToSchema,
        ) -> List<JsonNode>
    ) {
        oneOfs[property] = conditionsBuilder
    }

    fun <D, T> idConfig(
        idProperty: KProperty1<D, *>,
        dataProperty: KProperty1<D, *>,
        types: List<T>,
        typeBuilder: (type: T, toSchema: ToSchema) -> JsonNode,
        typeId: (T) -> String,
        typeRef: (T) -> String,
    ) {
        defs { toSchema ->
            types.associate { type ->
                val schemaType = typeBuilder(type, toSchema)
                typeRef(type) to schemaType
            }
        }
        propertyType(idProperty) {
            mapOf(
                "type" to "string",
                "enum" to types.map { typeId(it) }.sorted(),
            ).asJson()
        }
        oneOf(dataProperty) { _, _ ->
            types.map { type ->
                val ref = typeRef(type)
                mapOf(
                    "properties" to mapOf(
                        idProperty.name to mapOf(
                            "const" to typeId(type)
                        ),
                        dataProperty.name to mapOf(
                            """${'$'}ref""" to "#/${'$'}defs/$ref"
                        ),
                    )
                ).asJson()
            }
        }
    }

    fun toJsonSchemaConfiguration() = JsonSchemaConfiguration(
        defs = defs,
        propertyTypes = propertyTypes.toMap(),
        oneOfs = oneOfs.toMap(),
    )

}

data class JsonSchemaConfiguration(
    val defs: List<DefBuilder>,
    val propertyTypes: Map<KProperty1<*, *>, (property: KProperty1<*, *>) -> JsonNode>,
    val oneOfs: Map<KProperty1<*, *>, OneOfBuilder>,
)

typealias DefBuilder = (toSchema: ToSchema) -> Map<String, JsonNode>

typealias ToSchema = (type: KType, description: String?) -> Map<String, Any?>

typealias OneOfBuilder = (property: KProperty1<*, *>, toSchema: ToSchema) -> List<JsonNode>

package net.nemerosa.ontrack.extension.casc.schema

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.casc.CascContext
import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.annotations.getPropertyName
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.jvmErasure

sealed class CascType(
    val description: String,
) {
    abstract val __type: String
}

class CascObject(
    val fields: List<CascField>,
    description: String,
) : CascType(description) {
    override val __type: String = "object"
}

class CascArray(
    description: String,
    val type: CascType,
) : CascType(description) {
    override val __type: String = "array"
}

class CascField(
    val name: String,
    val type: CascType,
    val description: String,
    val required: Boolean,
)

class CascJson : CascType("JSON type") {
    override val __type: String = "JSON"
}

sealed class CascScalar(
    override val __type: String,
    description: String,
) : CascType(description)

class CascString : CascScalar("string", "String type")
class CascInt : CascScalar("int", "Int type")
class CascLong : CascScalar("long", "Long type")
class CascBoolean : CascScalar("boolean", "Boolean type")

val cascString = CascString()
val cascInt = CascInt()
val cascLong = CascLong()
val cascBoolean = CascBoolean()
val cascJson = CascJson()

private val builtinTypes = listOf(
    cascString,
    cascInt,
    cascLong,
    cascBoolean,
    cascJson,
).associateBy { it.__type }

// ====================================================================================
// ====================================================================================
// ====================================================================================

class DescribedCascType(
    val type: CascType,
    val description: String,
    val required: Boolean = false,
) {
    fun required() = DescribedCascType(type, description, required = true)
}

fun CascContext.with(description: String) = DescribedCascType(type, description)

fun cascObject(
    description: String,
    vararg fields: CascField,
) = CascObject(
    fields.toList(),
    description
)

fun cascObject(
    description: String,
    vararg fields: Pair<String, DescribedCascType>,
) = CascObject(
    description = description,
    fields = fields.map { (name, described) ->
        CascField(
            name = name,
            type = described.type,
            description = described.description,
            required = described.required,
        )
    }
)

fun cascArray(
    description: String,
    type: CascType,
) = CascArray(
    description,
    type
)

fun cascField(
    name: String,
    type: CascType,
    description: String,
    required: Boolean,
) = CascField(
    name = name,
    type = type,
    description = description,
    required = required,
)

// ====================================================================================
// ====================================================================================
// ====================================================================================

fun cascObject(type: KClass<*>): CascType {
    val description = type.findAnnotation<APIDescription>()?.value ?: type.java.simpleName
    return CascObject(
        type.memberProperties.map { property ->
            cascField(property)
        },
        description
    )
}

fun cascField(
    property: KProperty<*>,
    type: CascType = cascFieldType(property),
    description: String? = null,
    required: Boolean? = null,
): CascField {
    val actualDescription = description
        ?: property.findAnnotation<APIDescription>()?.value
        ?: property.name
    return CascField(
        name = cascFieldName(property),
        type = type,
        description = actualDescription,
        required = required ?: !property.returnType.isMarkedNullable,
    )
}

fun cascFieldName(property: KProperty<*>): String =
    getPropertyName(property)

internal fun cascFieldType(property: KProperty<*>): CascType =
    when {
        property.hasAnnotation<CascPropertyType>() -> cascPropertyType(property)
        property.hasAnnotation<CascNested>() -> cascNestedType(property)
        else -> when (property.returnType.jvmErasure) {
            String::class -> cascString
            Boolean::class -> cascBoolean
            Int::class -> cascInt
            Long::class -> cascLong
            JsonNode::class -> cascJson
            else -> error("Cannot get CasC type for $property")
        }
    }

private fun cascPropertyType(property: KProperty<*>): CascType {
    val propertyType = property.findAnnotation<CascPropertyType>()
        ?: error("CascPropertyType annotation is expected on $property.")
    val type = propertyType.type
        .takeIf { it.isNotBlank() }
        ?: propertyType.value
    return builtinTypes[type] ?: error("Cannot find built-in type [$type] required by $property.")
}

private fun cascNestedType(property: KProperty<*>): CascType =
    cascObject(property.returnType.jvmErasure)

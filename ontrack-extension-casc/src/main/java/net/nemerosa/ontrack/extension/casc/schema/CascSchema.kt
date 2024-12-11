package net.nemerosa.ontrack.extension.casc.schema

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.casc.CascContext
import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.annotations.APIIgnore
import net.nemerosa.ontrack.model.annotations.getPropertyDescription
import net.nemerosa.ontrack.model.annotations.getPropertyName
import java.time.Duration
import kotlin.jvm.internal.Reflection
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaType
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

    fun findFieldByName(name: String) = fields.find { it.name == name }
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

class CascDuration : CascType("Duration") {
    override val __type: String = "Duration"
}

class CascEnum(
    val name: String,
    val values: List<String>,
) : CascType("Enum") {
    override val __type: String = "Enum"
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
val cascDuration = CascDuration()

private val builtinTypes = listOf(
    cascString,
    cascInt,
    cascLong,
    cascBoolean,
    cascJson,
    cascDuration,
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
        type.memberProperties.mapNotNull { property ->
            if (!property.hasAnnotation<APIIgnore>()) {
                cascField(property)
            } else {
                null
            }
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
            Duration::class -> cascDuration
            List::class -> cascArray(property)
            else -> {
                val kclass = property.returnType.classifier as? KClass<*>
                when {
                    kclass?.isSubclassOf(Enum::class) == true -> cascEnum(property)
                    else -> error("Cannot get CasC type for $property")
                }
            }
        }
    }

private fun cascArray(property: KProperty<*>): CascType {
    val listArguments = property.returnType.arguments
    return if (listArguments.size == 1) {
        val elementType = listArguments.first().type?.javaType
        if (elementType is Class<*>) {
            val kotlinClass = Reflection.createKotlinClass(elementType)
            cascArray(
                description = getPropertyDescription(property),
                type = cascObject(kotlinClass)
            )
        } else {
            error("Only list with typed elements are supported for $property")
        }
    } else {
        error("No argument found for the list type $property")
    }
}

private fun cascEnum(property: KProperty<*>): CascType {
    val kclass = property.returnType.classifier as KClass<*>
    return CascEnum(
        name = kclass.simpleName!!,
        values = kclass.java.enumConstants.map { it.toString() },
    )
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

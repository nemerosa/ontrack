package net.nemerosa.ontrack.casc.schema

import com.fasterxml.jackson.annotation.JsonProperty
import net.nemerosa.ontrack.casc.CascContext
import net.nemerosa.ontrack.model.annotations.APIDescription
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.full.createType
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaGetter

sealed class CascType(
    val description: String,
)

class CascObject(
    val fields: List<CascField>,
    description: String,
) : CascType(description)

class CascArray(
    description: String,
    type: CascType,
) : CascType(description)

class CascField(
    val name: String,
    val type: CascType,
    val description: String,
    val required: Boolean,
)

sealed class CascScalar(description: String) : CascType(description)

class CascString : CascScalar("String type")
class CascInt : CascScalar("Int type")
class CascBoolean : CascScalar("Boolean type")

val cascString = CascString()
val cascInt = CascInt()
val cascBoolean = CascBoolean()

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

internal fun cascField(property: KProperty<*>): CascField {
    val description = property.findAnnotation<APIDescription>()?.value ?: property.name
    return CascField(
        name = cascFieldName(property),
        type = cascFieldType(property),
        description = description,
        required = !property.returnType.isMarkedNullable,
    )
}

internal fun cascFieldName(property: KProperty<*>): String =
    property.javaGetter?.getAnnotation(JsonProperty::class.java)?.value
        ?: property.name

internal fun cascFieldType(property: KProperty<*>): CascType =
    when {
        property.returnType.isSubtypeOf(String::class.createType()) -> cascString
        property.returnType.isSubtypeOf(Boolean::class.createType()) -> cascBoolean
        property.returnType.isSubtypeOf(Int::class.createType()) -> cascInt
        else -> error("Cannot get CasC type for $property")
    }

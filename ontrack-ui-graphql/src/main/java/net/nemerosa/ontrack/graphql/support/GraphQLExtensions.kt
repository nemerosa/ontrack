package net.nemerosa.ontrack.graphql.support

import com.fasterxml.jackson.annotation.JsonProperty
import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.annotations.APILabel
import net.nemerosa.ontrack.model.annotations.APIName
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.full.findAnnotation

/**
 * Getting the type name for a class.
 *
 * Will use by order to priority:
 *
 * # the provided [name] if any
 * # the [APIName] annotation if any
 * # the simple name of the class
 */
@Deprecated("Use getAPITypeName")
fun getTypeName(type: KClass<*>, name: String? = null): String =
    name
        ?: type.findAnnotation<APIName>()?.value
        ?: type.java.simpleName

/**
 * Getting the description for a class.
 *
 * Will use by order to priority:
 *
 * # the provided [description] if any
 * # the [APIDescription] annotation if any
 * # a generated string based on the type name
 */
fun getTypeDescription(type: KClass<*>, description: String? = null): String =
    description
        ?: type.findAnnotation<APIDescription>()?.value
        ?: type.java.simpleName

/**
 * Getting the description for a property.
 *
 * Will use first the provided [description], then any [APIDescription] attached to the property
 * and as a fallback, a generated string based on the property name.
 */
@Deprecated("Use net.nemerosa.ontrack.model.annotations.APIUtilsKt.getPropertyDescription")
fun getPropertyDescription(property: KProperty<*>, description: String? = null): String =
    description
        ?: property.findAnnotation<APIDescription>()?.value
        ?: "${getPropertyName(property)} field"

/**
 * Getting the label for a property.
 *
 * Will use first the provided [label], then any [APILabel] attached to the property
 * and as a fallback, a generated string based on the property name.
 */
@Deprecated("Use net.nemerosa.ontrack.model.annotations.APIUtilsKt.getPropertyLabel")
fun getPropertyLabel(property: KProperty<*>, label: String? = null): String =
    label
        ?: property.findAnnotation<APILabel>()?.value
        ?: getPropertyName(property)

/**
 * Getting the name for a property.
 *
 * Will use in order:
 *
 * # any [APIName] annotation
 * # any [JsonProperty] annotation on the property
 * # any [JsonProperty] annotation on the getter
 * # the property name
 */
@Deprecated("Use net.nemerosa.ontrack.model.annotations.APIUtilsKt.getPropertyName")
fun getPropertyName(property: KProperty<*>): String =
    property.findAnnotation<APIName>()?.value
        ?: property.findAnnotation<JsonProperty>()?.value
        ?: property.getter.findAnnotation<JsonProperty>()?.value
        ?: property.name

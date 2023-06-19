package net.nemerosa.ontrack.model.annotations

import com.fasterxml.jackson.annotation.JsonProperty
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.full.findAnnotation

/**
 * Getting the description for a property.
 *
 * Will use first the provided [description], then any [APIDescription] attached to the property
 * and as a fallback, a generated string based on the property name.
 */
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
fun getPropertyName(property: KProperty<*>): String =
    property.findAnnotation<APIName>()?.value
        ?: property.findAnnotation<JsonProperty>()?.value
        ?: property.getter.findAnnotation<JsonProperty>()?.value
        ?: property.name

/**
 * Getting the name for a class
 *
 * Will use in order:
 *
 * # any [APIName] annotation
 * # the simple Java class name
 */
fun <T : Any> getAPITypeName(type: KClass<T>): String =
    type.findAnnotation<APIName>()?.value
        ?: type.java.simpleName

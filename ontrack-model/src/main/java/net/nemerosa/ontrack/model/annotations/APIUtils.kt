package net.nemerosa.ontrack.model.annotations

import com.fasterxml.jackson.annotation.JsonProperty
import net.nemerosa.ontrack.common.api.APIDescription
import net.nemerosa.ontrack.common.api.APIName
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.primaryConstructor

inline fun <reified A : Annotation> findPropertyAnnotation(property: KProperty<*>): A? =
    property.findAnnotation<A>()
        ?: property.getter.findAnnotation<A>()
        ?: property.javaClass.declaringClass?.kotlin?.primaryConstructor?.parameters
            ?.find { it.name == property.name }
            ?.findAnnotation<A>()

/**
 * Getting the description for a property.
 *
 * Will use first the provided [description], then any [net.nemerosa.ontrack.common.api.APIDescription] attached to the property
 * and as a fallback, a generated string based on the property name.
 */
fun getPropertyDescription(property: KProperty<*>, description: String? = null): String =
    description
        ?: getOptionalPropertyDescription(property)
        ?: "${getPropertyName(property)} field"

/**
 * Getting the description for a property.
 */
fun getOptionalPropertyDescription(property: KProperty<*>): String? =
    findPropertyAnnotation<APIDescription>(property)?.value

/**
 * Getting the label for a property.
 *
 * Will use first the provided [label], then any [APILabel] attached to the property
 * and as a fallback, a generated string based on the property name.
 */
fun getPropertyLabel(property: KProperty<*>, label: String? = null): String =
    label
        ?: findPropertyAnnotation<APILabel>(property)?.value
        ?: getPropertyName(property)

/**
 * Getting the name for a property.
 *
 * Will use in order:
 *
 * # any [net.nemerosa.ontrack.common.api.APIName] annotation
 * # any [JsonProperty] annotation on the property
 * # any [JsonProperty] annotation on the getter
 * # the property name
 */
fun getPropertyName(property: KProperty<*>): String =
    findPropertyAnnotation<APIName>(property)?.value
        ?: findPropertyAnnotation<JsonProperty>(property)?.value
        ?: property.name

/**
 * Getting the description for a class
 */
fun <T : Any> getAPITypeDescription(type: KClass<T>): String? =
    type.findAnnotation<APIDescription>()?.value

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

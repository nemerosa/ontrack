package net.nemerosa.ontrack.graphql.support

import com.fasterxml.jackson.annotation.JsonProperty
import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.annotations.APIName
import kotlin.reflect.KClass
import kotlin.reflect.KProperty
import kotlin.reflect.full.findAnnotation

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
fun getPropertyDescription(property: KProperty<*>, description: String? = null): String =
        description
                ?: property.findAnnotation<APIDescription>()?.value
                ?: "${getPropertyName(property)} field"


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

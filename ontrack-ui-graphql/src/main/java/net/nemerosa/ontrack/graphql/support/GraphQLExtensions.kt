package net.nemerosa.ontrack.graphql.support

import net.nemerosa.ontrack.model.annotations.APIDescription
import kotlin.reflect.KProperty
import kotlin.reflect.full.findAnnotation

/**
 * Getting the description for a property.
 *
 * Will use first the provided [description], then any [APIDescription] attached to the property
 * and as a fallback, a generated string based on the property name.
 */
fun getPropertyDescription(property: KProperty<*>, description: String? = null): String =
        description ?: property.findAnnotation<APIDescription>()?.value ?: "${property.name} field"

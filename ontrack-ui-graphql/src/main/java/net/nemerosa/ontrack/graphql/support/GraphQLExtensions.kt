package net.nemerosa.ontrack.graphql.support

import net.nemerosa.ontrack.model.annotations.APIDescription
import kotlin.reflect.KClass
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


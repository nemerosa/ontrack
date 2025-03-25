package net.nemerosa.ontrack.model.json.schema

import kotlin.reflect.KClass
import kotlin.reflect.full.starProjectedType

/**
 * Utility method to convert a class into a JSON type.
 */
fun JsonTypeBuilder.toType(cls: KClass<*>) = toType(cls.starProjectedType)

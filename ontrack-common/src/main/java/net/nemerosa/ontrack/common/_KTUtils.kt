package net.nemerosa.ontrack.common

import java.util.*
import kotlin.reflect.KCallable

/**
 * Combination of predicates
 */
infix fun <T> ((T) -> Boolean).and(other: (T) -> Boolean): (T) -> Boolean = { t ->
    this.invoke(t) && other.invoke(t)
}

/**
 * Creating an optional from a nullable reference
 */
fun <T> T?.asOptional(): Optional<T> = Optional.ofNullable(this)

/**
 * Optional to nullable
 */
fun <T> Optional<T>.getOrNull(): T? = orElse(null)

/**
 * Converts a POJO as a map, using properties as index.
 */
fun <T : Any> T.asMap(vararg properties: KCallable<Any?>): Map<String, Any?> =
        properties.associate { property ->
            property.name to property.call()
        }

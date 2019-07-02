package net.nemerosa.ontrack.common

import java.util.*

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

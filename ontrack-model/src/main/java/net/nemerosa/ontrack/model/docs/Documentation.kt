package net.nemerosa.ontrack.model.docs

import kotlin.reflect.KClass

/**
 * Used by a class (or function, or property) to point to a type containing
 * its documentation.
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FIELD, AnnotationTarget.PROPERTY, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class Documentation(
    val value: KClass<*>,
)

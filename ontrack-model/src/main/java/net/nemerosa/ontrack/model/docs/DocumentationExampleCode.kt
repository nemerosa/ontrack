package net.nemerosa.ontrack.model.docs

import kotlin.reflect.KClass

/**
 * Used by a class (or function, or property) to describe an example of code using it.
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FIELD, AnnotationTarget.PROPERTY, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class DocumentationExampleCode(
    val value: String,
)

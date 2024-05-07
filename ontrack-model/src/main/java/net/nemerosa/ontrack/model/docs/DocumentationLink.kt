package net.nemerosa.ontrack.model.docs

/**
 * Used by a class (or function, or property) to refer to an existing page of documentation.
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FIELD, AnnotationTarget.PROPERTY, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Repeatable
annotation class DocumentationLink(
    val value: String,
    val name: String,
)

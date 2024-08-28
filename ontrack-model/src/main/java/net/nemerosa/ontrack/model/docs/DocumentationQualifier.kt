package net.nemerosa.ontrack.model.docs

/**
 * Used by a class (or function, or property) to qualify a documented
 * element so that it can distinguished from a similar entry.
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FIELD, AnnotationTarget.PROPERTY, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Repeatable
annotation class DocumentationQualifier(
    val value: String,
    val name: String,
)

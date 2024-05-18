package net.nemerosa.ontrack.model.docs

/**
 * Used by a field to specify that this property must not be documented.
 */
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class DocumentationIgnore

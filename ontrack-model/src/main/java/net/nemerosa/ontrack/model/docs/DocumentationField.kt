package net.nemerosa.ontrack.model.docs

/**
 * Used by a field to specify that a field must be documented as well.
 *
 * The type used for the generation of the documentation is the type of the field.
 */
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class DocumentationField

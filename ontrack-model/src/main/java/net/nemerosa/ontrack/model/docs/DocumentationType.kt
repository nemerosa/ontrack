package net.nemerosa.ontrack.model.docs

/**
 * Specifies the type of a field.
 */
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class DocumentationType(
    val value: String,
)

package net.nemerosa.ontrack.model.docs

/**
 * Used on a class to specify that the documentation must also be generated using the fields of the super class.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class DocumentationUseSuper

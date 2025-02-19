package net.nemerosa.ontrack.model.annotations

/**
 * Marks a property as being not required for an API.
 *
 * Normally, a property is marked as optional if it is nullable or if it has a default value.
 *
 * In some cases, like in Casc, the property remains optional and must be marked as such.
 */
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class APIOptional

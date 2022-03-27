package net.nemerosa.ontrack.graphql.support

/**
 * Applied on a property to indicate that the field must not be generated.
 */
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class IgnoreRef
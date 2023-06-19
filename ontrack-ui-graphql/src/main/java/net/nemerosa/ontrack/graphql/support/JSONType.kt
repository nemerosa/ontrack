package net.nemerosa.ontrack.graphql.support

/**
 * Applied on a property to indicate that its target must be represented as JSON.
 */
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class JSONType

package net.nemerosa.ontrack.graphql.support

/**
 * Applied on a property to indicate that its target must be a type reference.
 */
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class TypeRef

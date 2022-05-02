package net.nemerosa.ontrack.extension.casc.schema

/**
 * Indicates that a field in a type being [transformed into a Casc type][CascObject] is itself
 * another [CascObject].
 */
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class CascNested

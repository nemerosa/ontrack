package net.nemerosa.ontrack.extension.casc.schema

/**
 * Indicates that a field in a type being [transformed into a Casc type][CascObject] has another type,
 * explicitly mentioned in this annotation.
 */
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class CascPropertyType(
    /**
     * Name of the [type][CascType.__type]. Alias for [type].
     */
    val value: String,
    /**
     * Name of the [type][CascType.__type].
     */
    val type: String = "",
)

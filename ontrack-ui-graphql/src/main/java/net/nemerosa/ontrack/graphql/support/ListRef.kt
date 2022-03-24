package net.nemerosa.ontrack.graphql.support

/**
 * Applied on a property to indicate that its target must be a list
 */
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class ListRef(
    /**
     * If [embedded] is true, then the corresponding item type will be created and added into the directory.
     */
    val embedded: Boolean = false,
    /**
     * If not empty and if [embedded] is `true`, the [suffix] string will be added to the name of the wrapped
     * type to create the name of the type.
     *
     * For example, for `List<MyType>`, if `suffix == "Input"`, then the created type name will be `MyTypeInput`.
     */
    val suffix: String = "",
)

package net.nemerosa.ontrack.model.json.schema

/**
 * Annotates a configuration class which is used to represent an array. One of its properties
 * is the array itself.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class JsonSchemaListWrapper(
    val listProperty: String,
)

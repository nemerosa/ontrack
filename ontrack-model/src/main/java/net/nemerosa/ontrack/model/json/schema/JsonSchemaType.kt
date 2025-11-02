package net.nemerosa.ontrack.model.json.schema

import kotlin.reflect.KClass

/**
 * Annotates a property where the value is a JSON object
 * and the associated provider is responsible for the JSON schema
 * of the values.
 */
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class JsonSchemaType(
    val provider: KClass<out JsonSchemaTypeProvider>,
    val configuration: String = "",
)

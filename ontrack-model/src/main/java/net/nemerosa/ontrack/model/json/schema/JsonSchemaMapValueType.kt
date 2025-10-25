package net.nemerosa.ontrack.model.json.schema

import kotlin.reflect.KClass

/**
 * Annotates a property of type Map where the value is a JSON object
 * and the associated provider is responsible for the JSON schema
 * of the values.
 */
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class JsonSchemaMapValueType(
    val provider: KClass<out JsonSchemaMapValueTypeProvider>,
)

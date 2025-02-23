package net.nemerosa.ontrack.model.json.schema

import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class DynamicJsonSchema(
    val discriminatorProperty: String,
    val configurationProperty: String,
    val provider: KClass<out DynamicJsonSchemaProvider>,
    val required: Boolean = true,
)

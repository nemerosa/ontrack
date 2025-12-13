package net.nemerosa.ontrack.model.json.schema

import kotlin.reflect.KClass

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class JsonSchemaPropertiesContributor(
    val provider: KClass<out JsonSchemaPropertiesContributorProvider>,
    val configuration: String = "",
)

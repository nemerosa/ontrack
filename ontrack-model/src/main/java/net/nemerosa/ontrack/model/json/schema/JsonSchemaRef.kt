package net.nemerosa.ontrack.model.json.schema

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class JsonSchemaRef(
    val value: String,
)

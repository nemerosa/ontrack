package net.nemerosa.ontrack.model.json.schema

/**
 * Service which can provide a [JsonType] for a property.
 *
 * Specified by the [JsonSchemaType] annotation.
 */
interface JsonSchemaTypeProvider {

    /**
     * Creates a [JsonType] for a map value.
     */
    fun createType(configuration: String): JsonType
}
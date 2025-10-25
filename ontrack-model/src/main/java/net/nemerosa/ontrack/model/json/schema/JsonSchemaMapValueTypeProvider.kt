package net.nemerosa.ontrack.model.json.schema

/**
 * Service which can provide a [JsonType] for a map value.
 *
 * Specified by the [JsonSchemaMapValueType] annotation.
 */
interface JsonSchemaMapValueTypeProvider {

    /**
     * Creates a [JsonType] for a map value.
     */
    fun createType(): JsonType
}
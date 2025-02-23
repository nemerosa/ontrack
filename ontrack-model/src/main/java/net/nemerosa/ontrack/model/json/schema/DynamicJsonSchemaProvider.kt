package net.nemerosa.ontrack.model.json.schema

interface DynamicJsonSchemaProvider {

    /**
     * List of possible values for the discriminator
     */
    val discriminatorValues: List<String>

    /**
     * Index of all types for the configurations
     */
    fun getConfigurationTypes(builder: JsonTypeBuilder): Map<String, JsonType>

    /**
     * Given a discriminator value, returns a type reference
     */
    fun toRef(id: String): String

}
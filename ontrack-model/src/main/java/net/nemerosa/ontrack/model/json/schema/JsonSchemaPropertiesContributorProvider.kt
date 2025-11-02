package net.nemerosa.ontrack.model.json.schema

interface JsonSchemaPropertiesContributorProvider {

    fun contributeProperties(configuration: String, jsonTypeBuilder: JsonTypeBuilder): Map<String, JsonType>

}
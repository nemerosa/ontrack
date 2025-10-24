package net.nemerosa.ontrack.model.json.schema

interface JsonSchemaPropertiesContributorProvider {

    fun contributeProperties(configuration: String): Map<String, JsonType>

}
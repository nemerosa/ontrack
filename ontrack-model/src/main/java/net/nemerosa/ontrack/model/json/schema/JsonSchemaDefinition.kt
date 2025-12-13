package net.nemerosa.ontrack.model.json.schema

import net.nemerosa.ontrack.model.annotations.APIDescription

@APIDescription("Available JSON schema")
data class JsonSchemaDefinition(
    @APIDescription("Internal key")
    val key: String,
    @APIDescription("URI ID for the JSON schema")
    val id: String,
    @APIDescription("Title for the JSON schema")
    val title: String,
    @APIDescription("Description for the JSON schema")
    val description: String,
)

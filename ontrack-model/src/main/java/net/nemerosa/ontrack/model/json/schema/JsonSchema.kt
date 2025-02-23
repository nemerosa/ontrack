package net.nemerosa.ontrack.model.json.schema

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder

@JsonPropertyOrder(
    value = [
        "\$schema",
        "\$id",
        "title",
        "description",
        "\$defs",
        "\$ref",
    ]
)
data class JsonSchema(
    @JsonIgnore
    val ref: String,
    @JsonProperty("\$id")
    val id: String,
    @JsonIgnore
    val defs: Map<String, JsonType>,
    val title: String,
    val description: String?,
    @JsonIgnore
    val root: JsonObjectType,
) {
    @JsonProperty("\$schema")
    val schema = SCHEMA

    @Suppress("unused")
    @JsonProperty("\$defs")
    val allDefs: Map<String, JsonType> = mapOf(ref to root) + defs

    @JsonProperty("\$ref")
    @Suppress("unused")
    val innerRef: String = "#/\$defs/$ref"

    companion object {
        const val SCHEMA = "https://json-schema.org/draft/2020-12/schema"
    }
}
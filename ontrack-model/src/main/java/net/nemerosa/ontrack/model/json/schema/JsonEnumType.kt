package net.nemerosa.ontrack.model.json.schema

import com.fasterxml.jackson.annotation.JsonProperty

class JsonEnumType(
    @JsonProperty("enum")
    val values: List<String>,
    description: String?,
) : AbstractJsonNamedType(
    title = "Enum",
    type = "string",
    description = description,
)
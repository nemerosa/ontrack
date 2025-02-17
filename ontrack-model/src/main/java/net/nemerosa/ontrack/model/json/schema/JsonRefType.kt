package net.nemerosa.ontrack.model.json.schema

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty

class JsonRefType(
    @JsonIgnore
    val ref: String,
    description: String?
) : AbstractJsonDescribedType(description) {

    @JsonProperty("\$ref")
    @Suppress("unused")
    val innerRef: String = "#/\$defs/$ref"

}

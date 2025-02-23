package net.nemerosa.ontrack.model.json.schema

import com.fasterxml.jackson.annotation.JsonInclude

abstract class AbstractJsonDescribedType(
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val description: String?,
) : AbstractJsonType()

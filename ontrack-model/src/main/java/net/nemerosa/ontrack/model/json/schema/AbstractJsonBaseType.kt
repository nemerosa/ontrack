package net.nemerosa.ontrack.model.json.schema

abstract class AbstractJsonBaseType(
    val type: String,
    description: String?
) : AbstractJsonDescribedType(description)

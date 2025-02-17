package net.nemerosa.ontrack.model.json.schema

class JsonArrayType(
    val items: JsonType,
    description: String?,
) : AbstractJsonBaseType("array", description)
package net.nemerosa.ontrack.model.json.schema

class JsonMapObjectType(
    itemType: JsonType,
    description: String?,
) : AbstractJsonBaseType(
    type = "object",
    description = description,
) {
    val additionalProperties = itemType
}


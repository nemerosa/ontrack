package net.nemerosa.ontrack.model.json.schema

abstract class AbstractJsonNamedType(
    val title: String,
    type: String,
    description: String?,
) : AbstractJsonBaseType(type, description)

package net.nemerosa.ontrack.model.json.schema

interface JsonTypeProvider {

    fun jsonType(jsonTypeBuilder: JsonTypeBuilder): JsonType

}
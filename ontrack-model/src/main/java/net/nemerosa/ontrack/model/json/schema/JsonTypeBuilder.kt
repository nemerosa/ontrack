package net.nemerosa.ontrack.model.json.schema

import kotlin.reflect.KType

interface JsonTypeBuilder {
    fun toType(type: KType, description: String? = null): JsonType
}
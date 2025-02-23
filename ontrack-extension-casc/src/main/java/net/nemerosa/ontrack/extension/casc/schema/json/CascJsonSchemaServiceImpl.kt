package net.nemerosa.ontrack.extension.casc.schema.json

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.casc.schema.*
import net.nemerosa.ontrack.json.SimpleDurationDeserializer
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.model.support.EnvService
import org.springframework.stereotype.Service

@Service
class CascJsonSchemaServiceImpl(
    private val cascSchemaService: CascSchemaService,
    private val envService: EnvService,
) : CascJsonSchemaService {

    override fun createCascJsonSchema(): JsonNode {
        val casc = cascSchemaService.schema

        val header = mapOf(
            "${'$'}schema" to "https://json-schema.org/draft/2020-12/schema",
            "${'$'}id" to "https://ontrack.run/${envService.version.display}/schema/casc",
            "title" to "Ontrack CasC",
            "description" to "Configuration as code for Ontrack",
        )

        val type = toSchema(casc)

        return (header + type).asJson()
    }

    private fun toSchema(type: CascType, description: String? = null): Map<String, Any> {
        return when (type) {
            is CascString -> toString(type, description)
            is CascInt -> toInt(type, description)
            is CascLong -> toLong(type, description)
            is CascBoolean -> toBoolean(type, description)
            is CascEnum -> toEnum(type, description)
            is CascArray -> toArray(type, description)
            is CascJson -> toJson(type, description)
            is CascObject -> toObject(type, description)
            is CascDuration -> toDuration(type, description)
        }
    }

    private fun toDuration(type: CascDuration, description: String? = null): Map<String, Any> =
        mapOf(
            "title" to "Duration",
            "description" to (description ?: type.description),
            "type" to "string",
            "pattern" to SimpleDurationDeserializer.REGEX_DURATION,
        )

    private fun toObject(type: CascObject, description: String? = null): Map<String, Any> =
        mapOf(
            "title" to "Object",
            "description" to (description ?: type.description),
            "properties" to type.fields.associate { field ->
                field.name to toSchema(field.type, field.description)
            },
            "required" to type.fields.filter { field -> field.required }.map { field -> field.name },
            "additionalProperties" to false,
        )

    private fun toJson(type: CascJson, description: String? = null): Map<String, Any> =
        mapOf(
            "title" to "JSON",
            "description" to (description ?: type.description),
            "type" to emptyMap<String, String>(),
        )

    private fun toArray(type: CascArray, description: String? = null): Map<String, Any> =
        mapOf(
            "title" to "Array",
            "description" to (description ?: type.description),
            "type" to "array",
            "items" to toSchema(type.type)
        )

    private fun toEnum(type: CascEnum, description: String? = null): Map<String, Any> =
        mapOf(
            "title" to type.name,
            "description" to (description ?: type.description),
            "enum" to type.values,
        )

    private fun toString(type: CascString, description: String? = null): Map<String, Any> =
        mapOf(
            "title" to "String",
            "description" to (description ?: type.description),
            "type" to "string",
        )

    private fun toInt(type: CascInt, description: String? = null): Map<String, Any> =
        mapOf(
            "title" to "Integer",
            "description" to (description ?: type.description),
            "type" to "integer",
        )

    private fun toLong(type: CascLong, description: String? = null): Map<String, Any> =
        mapOf(
            "title" to "Long",
            "description" to (description ?: type.description),
            "type" to "integer",
        )

    private fun toBoolean(type: CascBoolean, description: String? = null): Map<String, Any> =
        mapOf(
            "title" to "Boolean",
            "description" to (description ?: type.description),
            "type" to "boolean",
        )

}
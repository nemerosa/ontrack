package net.nemerosa.ontrack.extension.config.schema

import net.nemerosa.ontrack.model.json.schema.JsonObjectType
import net.nemerosa.ontrack.model.json.schema.JsonSchemaMapValueTypeProvider
import net.nemerosa.ontrack.model.json.schema.JsonType
import net.nemerosa.ontrack.model.json.schema.JsonTypeBuilder
import net.nemerosa.ontrack.model.structure.ValidationDataType
import net.nemerosa.ontrack.model.structure.ValidationDataTypeAlias
import org.springframework.stereotype.Component

/**
 * Provides a type for the JSON values of a validation stamp map.
 */
@Component
class ValidationJsonSchemaMapValueTypeProvider(
    private val validationDataTypes: List<ValidationDataType<*, *>>,
    private val validationDataTypeAliases: List<ValidationDataTypeAlias>,
) : JsonSchemaMapValueTypeProvider {

    override fun createType(jsonTypeBuilder: JsonTypeBuilder): JsonType {

        // One property per type
        val properties = mutableMapOf<String, JsonType>()
        validationDataTypes.forEach { dataType ->
            val typeName = dataType::class.java.name
            val jsonType = dataType.createConfigJsonType(jsonTypeBuilder)
            properties[typeName] = jsonType
        }

        // Mapping also the aliases
        validationDataTypeAliases.forEach { alias ->
            val existingType = properties[alias.type]
                ?: error("Cannot find existing type for alias ${alias.alias}")
            properties[alias.alias] = existingType
        }

        return JsonObjectType(
            title = "ValidationStampConfiguration",
            description = "Validation stamp configuration",
            properties = properties,
            required = emptyList(),
            additionalProperties = false,
            maxProperties = 1, // At most one property
            oneOf = null,
        )
    }

}
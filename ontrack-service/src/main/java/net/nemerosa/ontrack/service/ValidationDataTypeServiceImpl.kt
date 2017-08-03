package net.nemerosa.ontrack.service

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.model.exceptions.ValidationRunDataInputException
import net.nemerosa.ontrack.model.structure.ServiceConfiguration
import net.nemerosa.ontrack.model.structure.ValidationDataType
import net.nemerosa.ontrack.model.structure.ValidationDataTypeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ValidationDataTypeServiceImpl
@Autowired
constructor(
        private val types: List<ValidationDataType<*, *>>
) : ValidationDataTypeService {

    override fun <C, T> getValidationDataType(id: String): ValidationDataType<C, T>? {
        @Suppress("UNCHECKED_CAST")
        return types.find { it::class.java.name == id } as? ValidationDataType<C, T>?
    }

    override fun getAllTypes(): List<ValidationDataType<*, *>> = types

    override fun validateData(data: ServiceConfiguration, config: JsonNode) {
        // Gets the data type first
        val validationDataType = getValidationDataType<Any, Any>(data.id) ?: throw ValidationRunDataInputException(
                "Cannot find any data type for ID `%s`",
                data.id
        )
        // Validation
        validateData(validationDataType, data.data, config)
    }

    private fun <C, T> validateData(validationDataType: ValidationDataType<C, T>, dataJson: JsonNode, configJson: JsonNode) {
        // Parses the configuration
        val config = validationDataType.configFromJson(configJson)
        // Parses the data
        val data = validationDataType.fromJson(dataJson) ?: throw ValidationRunDataInputException(
                "Data is required with validation run."
        )
        // Validation
        validationDataType.validateData(config, data)
    }
}

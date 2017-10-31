package net.nemerosa.ontrack.repository.support

import net.nemerosa.ontrack.model.exceptions.ValidationRunDataInputException
import net.nemerosa.ontrack.model.structure.*
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

    override fun <C, T> validateData(data: ValidationRunData<T>?, config: ValidationDataTypeConfig<C>?): ValidationRunData<T>? {
        // Config present
        if (config != null) {
            // No data
            if (data == null) {
                throw ValidationRunDataInputException("Data is required for this validation run.")
            }
            // Data
            else {
                if (data.id != config.id) {
                    // Different type of data
                    throw ValidationRunDataInputException(
                            "Data associated with the validation run as different " +
                                    "type than the one associated with the validation stamp. " +
                                    "`${config.id}` is expected and `${data.id}` was given."
                    )
                } else {
                    // Gets the type
                    val validationDataType = getValidationDataType<C, T>(data.id) ?:
                            throw ValidationRunDataInputException("Cannot find any data type for ID `${data.id}`")
                    // Validation
                    val validatedData = validationDataType.validateData(config.config, data.data)
                    // OK
                    return ValidationRunData(
                            data.id,
                            validatedData
                    )
                }
            }
        }
        // Data present (but no config)
        else if (data != null) {
            throw ValidationRunDataInputException("Data is not required for this validation run.")
        }
        // No config, no data --> OK
        else {
            return null
        }
    }

    override fun <C, T> validateData(data: ServiceConfiguration?, config: ValidationDataTypeConfig<C>?): ValidationRunData<T>? {
        // Config present
        if (config != null) {
            // No data
            if (data == null) {
                throw ValidationRunDataInputException("Data is required for this validation run.")
            }
            // Data
            else {
                if (data.id != config.id) {
                    // Different type of data
                    throw ValidationRunDataInputException(
                            "Data associated with the validation run as different " +
                                    "type than the one associated with the validation stamp. " +
                                    "`${config.id}` is expected and `${data.id}` was given."
                    )
                } else {
                    // Gets the type
                    val validationDataType = getValidationDataType<C, T>(data.id) ?:
                            throw ValidationRunDataInputException("Cannot find any data type for ID `${data.id}`")
                    // Parsing & validation
                    val parsedData = validationDataType.fromForm(data.data)
                    val validatedData = validationDataType.validateData(config.config, parsedData)
                    // OK
                    return ValidationRunData(
                            data.id,
                            validatedData
                    )
                }
            }
        }
        // Data present (but no config)
        else if (data != null) {
            throw ValidationRunDataInputException("Data is not required for this validation run.")
        }
        // No config, no data --> OK
        else {
            return null
        }
    }

}

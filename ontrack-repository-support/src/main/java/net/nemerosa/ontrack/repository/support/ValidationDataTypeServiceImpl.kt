package net.nemerosa.ontrack.repository.support

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.model.exceptions.*
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

    override fun <C> getServiceConfigurationForConfig(config: ValidationDataTypeConfig<C>?): ServiceConfiguration? {
        if (config != null) {
            // Gets the type
            val validationDataType = getValidationDataType<C, Any>(config.descriptor.id) ?:
                    throw ValidationRunDataInputException("Cannot find any data type for ID `${config.descriptor.id}`")
            // Converts the typed data into JSON for the client
            val json: JsonNode? = config.config?.let { validationDataType.configToFormJson(it) }
            // OK
            return ServiceConfiguration(
                    config.descriptor.id,
                    json
            )
        } else {
            return null
        }
    }

    override fun <C, T> validateData(data: ValidationRunData<T>?, config: ValidationDataTypeConfig<C>?): ValidationRunData<T>? {
        // Config present
        if (config != null) {
            // No data
            if (data == null) {
                throw ValidationRunDataInputException("Data is required for this validation run.")
            }
            // Data
            else {
                if (data.descriptor.id != config.descriptor.id) {
                    // Different type of data
                    throw ValidationRunDataInputException(
                            "Data associated with the validation run as different " +
                                    "type than the one associated with the validation stamp. " +
                                    "`${config.descriptor.id}` is expected and `${data.descriptor.id}` was given."
                    )
                } else {
                    // Gets the type
                    val validationDataType = getValidationDataType<C, T>(data.descriptor.id) ?:
                            throw ValidationRunDataInputException("Cannot find any data type for ID `${data.descriptor.id}`")
                    // Validation
                    val validatedData = validationDataType.validateData(config.config, data.data)
                    // OK
                    return ValidationRunData(
                            data.descriptor,
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

    override fun <C, T> validateData(data: ServiceConfiguration?, config: ValidationDataTypeConfig<C>?, status: String?, statusLoader: (String) -> ValidationRunStatusID): ValidationRunDataWithStatus<T> {
        if (config == null) {
            if (data == null) {
                // OK, no data requested, no data as input
                // ... but status is therefore required
                if (status == null) {
                    throw ValidationRunDataStatusRequiredException()
                } else {
                    return ValidationRunDataWithStatus(
                            null,
                            statusLoader(status)
                    )
                }
            } else {
                // Data is sent, not asked for...
                throw ValidationRunDataUnexpectedException()
            }
        } else if (data == null) {
            // No data as input. OK as long as the status is passed
            if (status == null) {
                throw ValidationRunDataStatusRequiredException()
            } else {
                return ValidationRunDataWithStatus(
                        null,
                        statusLoader(status)
                )
            }
        } else if (data.id != config.descriptor.id) {
            // Different type of data
            throw ValidationRunDataMismatchException(
                    config.descriptor.id,
                    data.id
            )
        } else {
            // Gets the type
            val validationDataType = getValidationDataType<C, T>(data.id) ?:
                    throw ValidationRunDataTypeNotFoundException(data.id)
            // Parsing & validation
            val parsedData = validationDataType.fromForm(data.data)
            val validatedData = validationDataType.validateData(config.config, parsedData)
            // Computing the status
            val computedStatus = validationDataType.computeStatus(config.config, validatedData)
            // Final status
            val finalStatus: ValidationRunStatusID
            if (computedStatus != null) {
                finalStatus = computedStatus
            } else if (status != null) {
                finalStatus = statusLoader(status)
            } else {
                finalStatus = ValidationRunStatusID.STATUS_PASSED
            }
            // OK
            return ValidationRunDataWithStatus(
                    ValidationRunData(
                            config.descriptor,
                            validatedData
                    ),
                    finalStatus
            )
        }
    }

}

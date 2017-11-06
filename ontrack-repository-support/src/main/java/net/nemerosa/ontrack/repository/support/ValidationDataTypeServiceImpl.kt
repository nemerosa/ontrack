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

    override fun <C, T> validateData(data: ValidationRunData<T>?, config: ValidationDataTypeConfig<C>?, status: ValidationRunStatusID?): ValidationRunData<T>? {
        return doValidateData<C, T>(
                data?.descriptor?.id,
                { _ -> data?.data },
                status?.let { { it } },
                config
        ).runData
    }

    override fun <C, T> validateData(data: ServiceConfiguration?, config: ValidationDataTypeConfig<C>?, status: String?, statusLoader: (String) -> ValidationRunStatusID): ValidationRunDataWithStatus<T> {
        return doValidateData(
                data?.id,
                { type -> type.fromForm(data?.data) },
                status?.let { { statusLoader(it) } },
                config
        )
    }

    private fun <C, T> doValidateData(
            dataId: String?,
            dataRawData: (ValidationDataType<C, T>) -> T?,
            status: (() -> ValidationRunStatusID)?,
            config: ValidationDataTypeConfig<C>?
    ): ValidationRunDataWithStatus<T> {
        if (config == null) {
            if (dataId == null) {
                // OK, no data requested, no data as input
                // ... but status is therefore required
                if (status == null) {
                    throw ValidationRunDataStatusRequiredException()
                } else {
                    return ValidationRunDataWithStatus(
                            null,
                            status()
                    )
                }
            } else {
                // Data is sent, not asked for...
                throw ValidationRunDataUnexpectedException()
            }
        } else if (dataId == null) {
            // No data as input. OK as long as the status is passed
            if (status == null) {
                throw ValidationRunDataStatusRequiredException()
            } else {
                return ValidationRunDataWithStatus(
                        null,
                        status()
                )
            }
        } else if (dataId != config.descriptor.id) {
            // Different type of data
            throw ValidationRunDataMismatchException(
                    config.descriptor.id,
                    dataId
            )
        } else {
            // Gets the type
            val validationDataType = getValidationDataType<C, T>(dataId) ?:
                    throw ValidationRunDataTypeNotFoundException(dataId)
            // Parsing & validation
            val parsedData = dataRawData(validationDataType)
            val validatedData = validationDataType.validateData(config.config, parsedData)
            // Computing the status
            val computedStatus = validationDataType.computeStatus(config.config, validatedData)
            // Final status
            val finalStatus: ValidationRunStatusID =
                    when {
                        computedStatus != null -> computedStatus
                        status != null -> status()
                        else -> ValidationRunStatusID.STATUS_PASSED
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

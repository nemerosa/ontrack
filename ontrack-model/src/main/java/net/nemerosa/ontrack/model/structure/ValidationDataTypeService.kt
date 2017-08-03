package net.nemerosa.ontrack.model.structure

import com.fasterxml.jackson.databind.JsonNode

/**
 * Management of [ValidationDataType]s.
 */
interface ValidationDataTypeService {
    /**
     * List of all data types
     */
    fun getAllTypes(): List<ValidationDataType<*, *>>

    /**
     * Gets a data type by ID
     *
     * @param C Config type for the data type
     * @param T Data type
     * @param id ID (FQCN) of the type
     * @return The data type or `null` if not found
     */
    fun <C, T> getValidationDataType(id: String): ValidationDataType<C, T>?

    /**
     * Validates some data according to its type and configuration.
     *
     * @param data Data to validate (ID + JSON)
     * @param config Configuration associated to the type
     */
    fun validateData(data: ServiceConfiguration, config: JsonNode)
}
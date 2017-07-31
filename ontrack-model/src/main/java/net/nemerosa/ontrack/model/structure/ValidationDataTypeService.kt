package net.nemerosa.ontrack.model.structure

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
}
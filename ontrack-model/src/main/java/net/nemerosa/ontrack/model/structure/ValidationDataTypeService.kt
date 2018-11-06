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

    /**
     * Validates some run data according to its type and configuration.
     * @param typedData Data to validate (Type descriptor + Data)
     * @param config Configuration associated to the type
     * @param status Passed status
     * @return Validated data
     */
    fun <C, T> validateData(typedData: ValidationRunData<T>?, config: ValidationDataTypeConfig<C>?, status: ValidationRunStatusID?): ValidationRunDataWithStatus<T>

    /**
     * Gets the [ServiceConfiguration] representation for a [ValidationDataTypeConfig].
     */
    fun <C> getServiceConfigurationForConfig(config: ValidationDataTypeConfig<C>?): ServiceConfiguration?
}
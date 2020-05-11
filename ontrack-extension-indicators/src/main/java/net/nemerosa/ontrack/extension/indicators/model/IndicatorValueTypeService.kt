package net.nemerosa.ontrack.extension.indicators.model

/**
 * Access to the list of value types.
 */
interface IndicatorValueTypeService {

    /**
     * Gets all value types
     */
    fun findAll(): List<IndicatorValueType<*, *>>

    /**
     * Gets a value type using its [id][IndicatorValueType.id].
     */
    fun findValueTypeById(id: String): IndicatorValueType<*, *>?

}
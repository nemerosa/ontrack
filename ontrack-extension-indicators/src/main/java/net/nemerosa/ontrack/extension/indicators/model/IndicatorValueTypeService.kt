package net.nemerosa.ontrack.extension.indicators.model

import net.nemerosa.ontrack.model.exceptions.NotFoundException

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
    fun <T, C> findValueTypeById(id: String): IndicatorValueType<T, C>?

    fun <T, C> getValueType(id: String): IndicatorValueType<T, C>

}

class IndicatorValueTypeNotFoundException(id: String) : NotFoundException("Indicator value type not found: $id")

package net.nemerosa.ontrack.extension.indicators.model

import net.nemerosa.ontrack.extension.indicators.model.IndicatorConstants.INDICATOR_ID_PATTERN
import net.nemerosa.ontrack.model.Ack
import net.nemerosa.ontrack.model.exceptions.InputException
import net.nemerosa.ontrack.model.exceptions.NotFoundException
import net.nemerosa.ontrack.model.structure.ServiceConfiguration
import javax.validation.constraints.Pattern

interface IndicatorTypeService {

    fun registerTypeListener(listener: IndicatorTypeListener)

    fun findAll(): List<IndicatorType<*, *>>

    fun getTypeById(typeId: String): IndicatorType<*, *>

    fun findTypeById(typeId: String): IndicatorType<*, *>?

    fun findByCategory(category: IndicatorCategory): List<IndicatorType<*, *>>

    fun createType(input: CreateTypeForm): IndicatorType<*, *>

    fun <T, C> createType(
            id: String,
            category: IndicatorCategory,
            name: String,
            link: String?,
            valueType: IndicatorValueType<T, C>,
            valueConfig: C,
            source: IndicatorSource? = null,
            computed: Boolean = false
    ): IndicatorType<T, C>

    fun updateType(input: CreateTypeForm): IndicatorType<*, *>

    fun <T, C> updateType(
            id: String,
            category: IndicatorCategory,
            name: String,
            link: String?,
            valueType: IndicatorValueType<T, C>,
            valueConfig: C,
            source: IndicatorSource? = null,
            computed: Boolean = false
    ): IndicatorType<T, C>

    fun deleteType(id: String): Ack

}

class IndicatorTypeNotFoundException(id: String) : NotFoundException("Indicator type not found: $id")

class IndicatorTypeIdAlreadyExistsException(id: String) : InputException("Indicator type with ID $id already exists.")

class IndicatorTypeIdMismatchException(expectedId: String, actualId: String) : InputException("Indicator type ID mismatch. Expected $expectedId, got $actualId")

class CreateTypeForm(
        @get:Pattern(regexp = INDICATOR_ID_PATTERN)
        val id: String,
        val category: String,
        val name: String,
        val link: String?,
        val valueType: ServiceConfiguration
)

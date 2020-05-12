package net.nemerosa.ontrack.extension.indicators.model

import net.nemerosa.ontrack.model.Ack
import net.nemerosa.ontrack.model.exceptions.InputException
import net.nemerosa.ontrack.model.exceptions.NotFoundException
import net.nemerosa.ontrack.model.structure.ServiceConfiguration
import javax.validation.constraints.Pattern

interface IndicatorTypeService {

    fun findAll(): List<IndicatorType<*, *>>

    fun getTypeById(typeId: String): IndicatorType<*, *>

    fun findTypeById(typeId: String): IndicatorType<*, *>?

    fun findByCategory(category: IndicatorCategory): List<IndicatorType<*, *>>

    fun createType(input: CreateTypeForm): IndicatorType<*, *>

    fun updateType(input: CreateTypeForm): IndicatorType<*, *>

    fun deleteType(id: String): Ack

}

class IndicatorTypeNotFoundException(id: String) : NotFoundException("Indicator type not found: $id")

class IndicatorTypeIdAlreadyExistsException(id: String) : InputException("Indicator type with ID $id already exists.")

class IndicatorTypeIdMismatchException(expectedId: String, actualId: String) : InputException("Indicator type ID mismatch. Expected $expectedId, got $actualId")

const val CREATE_TYPE_FORM_ID_PATTERN = "[a-z0-9-]+"

class CreateTypeForm(
        @get:Pattern(regexp = CREATE_TYPE_FORM_ID_PATTERN)
        val id: String,
        val category: String,
        val shortName: String,
        val longName: String,
        val link: String?,
        val valueType: ServiceConfiguration
)

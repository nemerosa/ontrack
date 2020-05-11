package net.nemerosa.ontrack.extension.indicators.model

import net.nemerosa.ontrack.model.exceptions.NotFoundException
import net.nemerosa.ontrack.model.structure.ServiceConfiguration

interface IndicatorTypeService {

    fun findAll(): List<IndicatorType<*, *>>

    fun getTypeById(typeId: String): IndicatorType<*, *>

    fun findTypeById(typeId: String): IndicatorType<*, *>?

    fun findByCategory(category: IndicatorCategory): List<IndicatorType<*, *>>

    fun createType(input: CreateTypeForm): IndicatorType<*, *>

    fun updateType(id: String, input: CreateTypeForm): IndicatorType<*, *>

}

class IndicatorTypeNotFoundException(id: String) : NotFoundException("Indicator type not found: $id")

class CreateTypeForm(
        val category: String,
        val shortName: String,
        val longName: String,
        val link: String?,
        val valueType: ServiceConfiguration
)

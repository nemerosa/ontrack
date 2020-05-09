package net.nemerosa.ontrack.extension.indicators.model

import net.nemerosa.ontrack.model.exceptions.NotFoundException

interface IndicatorTypeService {

    fun findAll(): List<IndicatorType<*, *>>

    fun getTypeById(typeId: String): IndicatorType<*, *>

}

class IndicatorTypeNotFoundException(id: String) : NotFoundException("Indicator type not found: $id")

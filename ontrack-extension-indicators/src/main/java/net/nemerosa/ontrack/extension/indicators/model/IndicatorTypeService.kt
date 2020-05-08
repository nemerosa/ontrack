package net.nemerosa.ontrack.extension.indicators.model

import net.nemerosa.ontrack.model.exceptions.NotFoundException

interface IndicatorTypeService {

    fun findAll(): List<IndicatorType<*, *>>

    fun getTypeById(typeId: Int): IndicatorType<*, *>

}

class IndicatorTypeNotFoundException(id: Int) : NotFoundException("Indicator type not found: $id")

package net.nemerosa.ontrack.extension.indicators.model

import net.nemerosa.ontrack.extension.indicators.model.IndicatorCategory
import net.nemerosa.ontrack.model.exceptions.NotFoundException

interface IndicatorCategoryService {

    fun findCategoryById(id: Int): IndicatorCategory?

    fun getCategory(id: Int): IndicatorCategory

}

class IndicatorCategoryNotFoundException(id: Int) : NotFoundException("Indicator category not found: $id")

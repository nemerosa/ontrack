package net.nemerosa.ontrack.extension.indicators.model

import net.nemerosa.ontrack.extension.indicators.model.IndicatorCategory
import net.nemerosa.ontrack.model.exceptions.NotFoundException

interface IndicatorCategoryService {

    fun findCategoryById(id: String): IndicatorCategory?

    fun getCategory(id: String): IndicatorCategory

}

class IndicatorCategoryNotFoundException(id: String) : NotFoundException("Indicator category not found: $id")

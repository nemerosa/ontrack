package net.nemerosa.ontrack.extension.indicators.model

import net.nemerosa.ontrack.model.Ack
import net.nemerosa.ontrack.model.exceptions.InputException
import net.nemerosa.ontrack.model.exceptions.NotFoundException
import javax.validation.constraints.Pattern

interface IndicatorCategoryService {

    fun registerCategoryListener(listener: IndicatorCategoryListener)

    fun findCategoryById(id: String): IndicatorCategory?

    fun getCategory(id: String): IndicatorCategory

    fun findAll(): List<IndicatorCategory>

    fun createCategory(input: IndicatorForm): IndicatorCategory

    fun updateCategory(input: IndicatorForm): IndicatorCategory

    fun deleteCategory(id: String): Ack

}

class IndicatorForm(
        @get:Pattern(regexp = IndicatorConstants.INDICATOR_ID_PATTERN)
        val id: String,
        val name: String
)

class IndicatorCategoryNotFoundException(id: String) : NotFoundException("Indicator category not found: $id")

class IndicatorCategoryIdAlreadyExistsException(id: String) : InputException("Indicator category with ID $id already exists.")

class IndicatorCategoryIdMismatchException(expectedId: String, actualId: String) : InputException("Indicator category ID mismatch. Expected $expectedId, got $actualId")

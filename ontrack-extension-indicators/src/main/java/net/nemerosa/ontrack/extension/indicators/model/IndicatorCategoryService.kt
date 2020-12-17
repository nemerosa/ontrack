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

    fun createCategory(input: IndicatorForm, source: IndicatorSource? = null): IndicatorCategory

    fun updateCategory(input: IndicatorForm, source: IndicatorSource? = null): IndicatorCategory

    fun deleteCategory(id: String, force: Boolean = false): Ack

    /**
     * Marks a category as being deprecated
     *
     * @param id ID of category to deprecate
     * @param deprecated Deprecation reason or null to remove it
     */
    fun deprecateCategory(id: String, deprecated: String?)

}

class IndicatorForm(
        @get:Pattern(regexp = IndicatorConstants.INDICATOR_ID_PATTERN)
        val id: String,
        val name: String,
        val deprecated: String? = null
)

class IndicatorCategoryNotFoundException(id: String) : NotFoundException("Indicator category not found: $id")

class IndicatorCategoryIdAlreadyExistsException(id: String) : InputException("Indicator category with ID $id already exists.")

class IndicatorCategoryIdMismatchException(expectedId: String, actualId: String) : InputException("Indicator category ID mismatch. Expected $expectedId, got $actualId")

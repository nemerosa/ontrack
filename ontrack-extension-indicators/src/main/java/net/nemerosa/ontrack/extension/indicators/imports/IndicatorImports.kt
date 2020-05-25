package net.nemerosa.ontrack.extension.indicators.imports

import net.nemerosa.ontrack.extension.indicators.model.IndicatorConstants
import javax.validation.constraints.Pattern

/**
 * Structure which defines categories & type to import.
 */
data class IndicatorImports(
        val source: String,
        val categories: List<IndicatorImportCategory>
)

data class IndicatorImportCategory(
        @get:Pattern(regexp = IndicatorConstants.INDICATOR_ID_PATTERN)
        val id: String,
        val name: String,
        val types: List<IndicatorImportsType>
)

data class IndicatorImportsType(
        @get:Pattern(regexp = IndicatorConstants.INDICATOR_ID_PATTERN)
        val id: String,
        val name: String,
        val link: String?,
        val required: Boolean?
)
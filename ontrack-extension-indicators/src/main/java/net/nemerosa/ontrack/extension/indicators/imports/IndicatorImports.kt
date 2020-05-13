package net.nemerosa.ontrack.extension.indicators.imports

import net.nemerosa.ontrack.extension.indicators.model.IndicatorConstants
import javax.validation.constraints.Pattern

/**
 * Structure which defines categories & type to import.
 */
class IndicatorImports(
        val categories: List<IndicatorImportCategory>
)

class IndicatorImportCategory(
        @get:Pattern(regexp = IndicatorConstants.INDICATOR_ID_PATTERN)
        val id: String,
        val name: String,
        val types: List<IndicatorImportsType>
)

class IndicatorImportsType(
        @get:Pattern(regexp = IndicatorConstants.INDICATOR_ID_PATTERN)
        val id: String,
        val shortName: String,
        val longName: String,
        val link: String?,
        val required: Boolean?
)
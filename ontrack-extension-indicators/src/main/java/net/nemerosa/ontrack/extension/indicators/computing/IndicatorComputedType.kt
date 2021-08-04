package net.nemerosa.ontrack.extension.indicators.computing

import net.nemerosa.ontrack.extension.indicators.model.IndicatorValueType

/**
 * Type provided by an [IndicatorComputer].
 */
data class IndicatorComputedType<T, C>(
    val category: IndicatorComputedCategory,
    val id: String,
    val name: String,
    val link: String?,
    val valueType: IndicatorValueType<T, C>,
    val valueConfig: C
)